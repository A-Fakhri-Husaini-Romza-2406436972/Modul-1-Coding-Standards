package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.service.CarService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarService carService;

    @Test
    void createCarPageShouldShowCreateCarView() throws Exception {
        mockMvc.perform(get("/car/createCar"))
                .andExpect(status().isOk())
                .andExpect(view().name("CreateCar"))
                .andExpect(model().attributeExists("car"));
    }

    @Test
    void createCarPostShouldCreateCarAndRedirect() throws Exception {
        mockMvc.perform(post("/car/createCar")
                        .param("carId", "car-1")
                        .param("carName", "Toyota Avanza")
                        .param("carColor", "Black")
                        .param("carQuantity", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);
        verify(carService).create(carCaptor.capture());
        Car createdCar = carCaptor.getValue();
        assertEquals("car-1", createdCar.getCarId());
        assertEquals("Toyota Avanza", createdCar.getCarName());
        assertEquals("Black", createdCar.getCarColor());
        assertEquals(3, createdCar.getCarQuantity());
    }

    @Test
    void carListPageShouldShowAllCars() throws Exception {
        Car firstCar = createCar("car-1", "Toyota Avanza", "Black", 3);
        Car secondCar = createCar("car-2", "Honda Brio", "Red", 2);
        when(carService.findAll()).thenReturn(List.of(firstCar, secondCar));

        mockMvc.perform(get("/car/listCar"))
                .andExpect(status().isOk())
                .andExpect(view().name("CarList"))
                .andExpect(model().attributeExists("cars"))
                .andExpect(model().attribute("cars", hasSize(2)));
    }

    @Test
    void editCarPageShouldShowEditCarView() throws Exception {
        Car existingCar = createCar("car-3", "Mitsubishi Xpander", "Gray", 4);
        when(carService.findById("car-3")).thenReturn(existingCar);

        mockMvc.perform(get("/car/editCar/car-3"))
                .andExpect(status().isOk())
                .andExpect(view().name("EditCar"))
                .andExpect(model().attributeExists("car"))
                .andExpect(model().attribute("car", samePropertyValuesAs(existingCar)));
    }

    @Test
    void editCarPostShouldUpdateCarAndRedirect() throws Exception {
        mockMvc.perform(post("/car/editCar")
                        .param("carId", "car-4")
                        .param("carName", "Toyota Veloz")
                        .param("carColor", "White")
                        .param("carQuantity", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);
        verify(carService).update(org.mockito.ArgumentMatchers.eq("car-4"), carCaptor.capture());
        Car updatedCar = carCaptor.getValue();
        assertEquals("Toyota Veloz", updatedCar.getCarName());
        assertEquals("White", updatedCar.getCarColor());
        assertEquals(5, updatedCar.getCarQuantity());
    }

    @Test
    void deleteCarShouldDeleteCarAndRedirect() throws Exception {
        mockMvc.perform(post("/car/deleteCar")
                        .param("carId", "car-5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService).deleteCarById("car-5");
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
