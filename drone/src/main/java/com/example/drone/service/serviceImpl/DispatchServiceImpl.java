package com.example.drone.service.serviceImpl;

import com.example.drone.dto.DroneRequestDto;
import com.example.drone.dto.MedicationItemRequest;
import com.example.drone.entity.Drone;
import com.example.drone.entity.MedicationItems;
import com.example.drone.enums.Model;
import com.example.drone.enums.State;
import com.example.drone.exception.NotFoundException;
import com.example.drone.repository.DroneRepository;
import com.example.drone.repository.MedicationItemRepository;
import com.example.drone.service.DispatchService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DispatchServiceImpl implements DispatchService {
    private final DroneRepository droneRepository;
    private final MedicationItemRepository medicationItemRepository;


    @Override
    public ResponseEntity<String> dispatch(MedicationItemRequest request) {
        loadDrone(request);
        return ResponseEntity.ok("processing request");
    }

    @PostConstruct
    public void onStart(){
        createDrone();
    }

    private void createDrone(){
        Drone drone = new Drone();
        drone.setSerialNumber(setDrone().getSerialNumber());
        drone.setModel(setDrone().getModel());
        drone.setWeight(setDrone().getWeight());
        drone.setState(setDrone().getState());
        drone.setMaxWeight(setDrone().getMaxWeight());
        drone.setBatteryLevel(setDrone().getBatteryLevel());

        log.info(droneRepository.save(drone).toString());
    }

    private DroneRequestDto setDrone(){
        DroneRequestDto requestDto = new DroneRequestDto();
        requestDto.setModel(Model.LIGHTWEIGHT);
        requestDto.setWeight(0.00);
        requestDto.setState(State.IDLE);
        requestDto.setSerialNumber(UUID.randomUUID().toString());
        requestDto.setMaxWeight(125.00);
        requestDto.setBatteryLevel(100);
        return requestDto;
    }

    private void loadDrone(MedicationItemRequest request){
        MedicationItems newMedicationItem = mapToMedicationItems(request);
        MedicationItems savedMedItems = medicationItemRepository.save(newMedicationItem);
        List<Drone> availableDrones = droneRepository.findAvailableDrones(State.IDLE,State.LOADING,25)
                .orElseThrow(()->new NotFoundException("Please hold on!. No drone is available at the moment"));
        System.out.println("--------- "+availableDrones);

        availableDrones.forEach(drone ->{
            addMedications(savedMedItems,drone);
        });

    }
    private MedicationItems mapToMedicationItems(MedicationItemRequest x){
        return MedicationItems.builder()
                .code(x.getCode())
                .imageUrl(x.getImageUrl())
                .medWeight(Double.parseDouble(x.getWeight()))
                .name(x.getName())
                .build();
    }

    private void addMedications(MedicationItems items, Drone drone){
        double checkAdd = drone.getWeight() + items.getMedWeight();
        log.info("check add " +checkAdd);

        if(checkAdd <= drone.getMaxWeight()){
            if(drone.getMedicationItemsList().isEmpty()){
                drone.setState(State.LOADING);
                drone.setStartTime(LocalDateTime.now());
                drone.setEndTime(LocalDateTime.now().plusSeconds(10)); // drone uses 5 seconds to load
            }
            drone.addWeight(items.getMedWeight());
            drone.addToMedicationItemsList(items);
            droneRepository.save(drone);
            items.setDrone(drone);
            medicationItemRepository.save(items);
        }
    }
    @Scheduled(fixedRate = 2000)
    public void schedules(){
        setLoaded();
        setDelivering();
        setDelivered();
        setReturning();
        setIdle();
    }

    private void setLoaded(){
        List<Drone> loadingDrones = droneRepository.findByState(State.LOADING);
        log.info("set loaded .......");
        if(loadingDrones.isEmpty()){
            log.info("loading drone is empty: ");
        }
        loadingDrones.forEach((drone)->{
            log.info("is before: "+drone.getEndTime().isBefore(LocalDateTime.now()));

            log.info("checking for drones that are end time");
            if(drone.getEndTime().isBefore(LocalDateTime.now())){
                log.info("loading drone: "+loadingDrones);
                drone.setState(State.LOADED);
                log.info("drone state set to Loaded");
                droneRepository.save(drone);
            }
        });
    }

    private void setDelivering(){
        List<Drone> loadedDrones = droneRepository.findByState(State.LOADED);

        log.info("loaded drone: "+loadedDrones);
        loadedDrones.forEach((drone)->{
            log.info("checking for loaded drones that are end time");
            setDeliveryTime(drone);
            drone.setState(State.DELIVERING);
            log.info("drone is delivering");
            droneRepository.save(drone);
        });
    }

    private void setDelivered(){
        List<Drone> deliveryDrones = droneRepository.findByState(State.DELIVERING);
        deliveryDrones.forEach((drone)->{
            log.info("checking for delivery drones that are end time");
            if(drone.getEndTime().isBefore(LocalDateTime.now())){
                log.info("delivery drone: "+deliveryDrones);
                log.info("Drone has delivered Medication Items");
                drone.setState(State.DELIVERED);
                droneRepository.save(drone);
            }
        });
    }

    private void setReturning(){
        List<Drone> deliveredDrone = droneRepository.findByState(State.DELIVERED);
        log.info("delivered drone: "+deliveredDrone);
        deliveredDrone.forEach((drone)->{
            drone.setState(State.RETURNING);
            drone.setStartTime(LocalDateTime.now());
            drone.setEndTime(LocalDateTime.now().plusSeconds(5));
            droneRepository.save(drone);
            log.info("drone is returning..");
        });
    }

    private void setIdle(){
        List<Drone> returningDrone = droneRepository.findByState(State.RETURNING);
        returningDrone.forEach((drone)->{
            if(drone.getEndTime().isBefore(LocalDateTime.now())){
                log.info("returning drone: "+returningDrone);
                drone.setState(State.IDLE);
                setBattery(drone);
                drone.setWeight(0.00);
                drone.setStartTime(null);
                drone.setEndTime(null);
                droneRepository.save(drone);
                log.info("drone is idle..");
            }
        });
    }

    private void setBattery(Drone drone){
        double weightPer = (100 * drone.getWeight())/drone.getMaxWeight();
        log.info("weightPer "+weightPer);
        int weightPercentage = (int) Math.abs(weightPer);
        log.info("weight percentage: "+weightPercentage);

        if(weightPercentage <= 20){
            drone.setBatteryLevel(100);
        }else{
            int i = weightPercentage - 10;
            drone.setBatteryLevel(100-i);
        }
    }

    private void setDeliveryTime(Drone drone){
        log.info("set drone Timing");
        Double weight = drone.getWeight();
        drone.setStartTime(LocalDateTime.now());

        if(weight <= 10){
            drone.setEndTime(LocalDateTime.now().plusSeconds(1));
        }else if(weight <= 20){
            drone.setEndTime(LocalDateTime.now().plusSeconds(2));
        }else if(weight <= 30){
            drone.setEndTime(LocalDateTime.now().plusSeconds(3));
        }else if(weight <= 40){
            drone.setEndTime(LocalDateTime.now().plusSeconds(4));
        }else if(weight <= 50){
            drone.setEndTime(LocalDateTime.now().plusSeconds(5));
        }else if(weight <= 60){
            drone.setEndTime(LocalDateTime.now().plusSeconds(6));
        }else if(weight <= 70){
            drone.setEndTime(LocalDateTime.now().plusSeconds(7));
        }else if(weight <= 80){
            drone.setEndTime(LocalDateTime.now().plusSeconds(8));
        }else if(weight <= 90){
            drone.setEndTime(LocalDateTime.now().plusSeconds(9));
        }else if(weight <= 100){
            drone.setEndTime(LocalDateTime.now().plusSeconds(10));
        }
    }

}
