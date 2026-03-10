package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void createShouldCallRepositoryAndReturnCar() {
        Car car = createCar("car-1", "Toyota Avanza", "Black", 3);
        when(carRepository.create(car)).thenReturn(car);

        Car result = carService.create(car);

        verify(carRepository).create(car);
        assertSame(car, result);
    }

    @Test
    void findAllShouldConvertIteratorToList() {
        Car firstCar = createCar("car-1", "Toyota Avanza", "Black", 3);
        Car secondCar = createCar("car-2", "Honda Brio", "Red", 2);
        when(carRepository.findAll()).thenReturn(List.of(firstCar, secondCar).iterator());

        List<Car> result = carService.findAll();

        verify(carRepository).findAll();
        assertEquals(2, result.size());
        assertEquals("car-1", result.get(0).getCarId());
        assertEquals("car-2", result.get(1).getCarId());
    }

    @Test
    void findAllShouldReturnEmptyList() {
        when(carRepository.findAll()).thenReturn(List.<Car>of().iterator());

        List<Car> result = carService.findAll();

        verify(carRepository).findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdShouldReturnCarFromRepository() {
        Car car = createCar("car-3", "Mitsubishi Xpander", "Gray", 4);
        when(carRepository.findById("car-3")).thenReturn(car);

        Car result = carService.findById("car-3");

        verify(carRepository).findById("car-3");
        assertSame(car, result);
    }

    @Test
    void updateShouldCallRepositoryUpdate() {
        Car updatedCar = createCar("car-4", "Toyota Veloz", "White", 5);

        carService.update("car-4", updatedCar);

        verify(carRepository).update("car-4", updatedCar);
    }

    @Test
    void deleteShouldCallRepositoryDelete() {
        carService.deleteCarById("car-5");

        verify(carRepository).delete("car-5");
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
