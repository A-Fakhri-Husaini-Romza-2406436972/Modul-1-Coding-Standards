package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryImplTest {

    private static final String CAR_ID_1 = "car-1";
    private static final String CAR_ID_2 = "car-2";
    private static final String CAR_NAME_AVANZA = "Toyota Avanza";
    private static final String CAR_COLOR_BLACK = "Black";
    private static final String MISSING_CAR_ID = "missing-car";
    private static final String CAR_NAME_VELOZ = "Toyota Veloz";
    private static final String CAR_COLOR_WHITE = "White";

    private CarRepositoryImpl carRepository;

    @BeforeEach
    void setUp() {
        carRepository = new CarRepositoryImpl();
    }

    @Test
    void createShouldKeepProvidedId() {
        Car car = createCar(CAR_ID_1, CAR_NAME_AVANZA, CAR_COLOR_BLACK, 3);

        Car createdCar = carRepository.create(car);

        assertEquals(CAR_ID_1, createdCar.getCarId());
        assertEquals(CAR_NAME_AVANZA, createdCar.getCarName());
    }

    @Test
    void createShouldGenerateIdWhenMissing() {
        Car car = createCar(null, "Honda Brio", "Red", 2);

        Car createdCar = carRepository.create(car);

        assertNotNull(createdCar.getCarId());
        assertFalse(createdCar.getCarId().isEmpty());
    }

    @Test
    void findAllShouldReturnAllCreatedCars() {
        carRepository.create(createCar(CAR_ID_1, CAR_NAME_AVANZA, CAR_COLOR_BLACK, 3));
        carRepository.create(createCar(CAR_ID_2, "Honda Brio", "Red", 2));

        Iterator<Car> iterator = carRepository.findAll();
        assertTrue(iterator.hasNext());
        assertEquals(CAR_ID_1, iterator.next().getCarId());
        assertTrue(iterator.hasNext());
        assertEquals(CAR_ID_2, iterator.next().getCarId());
        assertFalse(iterator.hasNext());
    }

    @Test
    void findByIdShouldReturnCarWhenFound() {
        Car car = createCar(CAR_ID_1, CAR_NAME_AVANZA, CAR_COLOR_BLACK, 3);
        carRepository.create(car);

        Car foundCar = carRepository.findById(CAR_ID_1);

        assertNotNull(foundCar);
        assertEquals(CAR_ID_1, foundCar.getCarId());
    }

    @Test
    void findByIdShouldReturnNullWhenMissing() {
        carRepository.create(createCar(CAR_ID_1, CAR_NAME_AVANZA, CAR_COLOR_BLACK, 3));

        assertNull(carRepository.findById(MISSING_CAR_ID));
    }

    @Test
    void updateShouldModifyCarWhenFound() {
        carRepository.create(createCar(CAR_ID_1, CAR_NAME_AVANZA, CAR_COLOR_BLACK, 3));
        Car updatedCar = createCar("ignored-id", CAR_NAME_VELOZ, CAR_COLOR_WHITE, 5);

        Car result = carRepository.update(CAR_ID_1, updatedCar);

        assertNotNull(result);
        assertEquals(CAR_NAME_VELOZ, result.getCarName());
        assertEquals(CAR_COLOR_WHITE, result.getCarColor());
        assertEquals(5, result.getCarQuantity());
    }

    @Test
    void updateShouldReturnNullWhenNotFound() {
        Car updatedCar = createCar("ignored-id", CAR_NAME_VELOZ, CAR_COLOR_WHITE, 5);

        assertNull(carRepository.update(MISSING_CAR_ID, updatedCar));
    }

    @Test
    void updateShouldReturnNullWhenDifferentIdExists() {
        carRepository.create(createCar(CAR_ID_1, CAR_NAME_AVANZA, CAR_COLOR_BLACK, 3));
        Car updatedCar = createCar("ignored-id", CAR_NAME_VELOZ, CAR_COLOR_WHITE, 5);

        assertNull(carRepository.update(MISSING_CAR_ID, updatedCar));
    }

    @Test
    void deleteShouldRemoveCarWhenFound() {
        carRepository.create(createCar(CAR_ID_1, CAR_NAME_AVANZA, CAR_COLOR_BLACK, 3));

        carRepository.delete(CAR_ID_1);

        assertNull(carRepository.findById(CAR_ID_1));
    }

    @Test
    void deleteShouldNotAffectDataWhenIdMissing() {
        carRepository.create(createCar(CAR_ID_1, CAR_NAME_AVANZA, CAR_COLOR_BLACK, 3));

        carRepository.delete(MISSING_CAR_ID);

        assertNotNull(carRepository.findById(CAR_ID_1));
    }

    private Car createCar(String id, String name, String color, int quantity) {
        Car car = new Car();
        car.setCarId(id);
        car.setCarName(name);
        car.setCarColor(color);
        car.setCarQuantity(quantity);
        return car;
    }
}
