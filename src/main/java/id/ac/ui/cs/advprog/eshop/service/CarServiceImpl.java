package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    @Override
    public Car create(Car car) {
        // Memanggil repositori untuk menyimpan data mobil baru
        carRepository.create(car);
        return car;
    }

    @Override
    public List<Car> findAll() {
        // Mengubah Iterator dari repositori menjadi List
        Iterator<Car> carIterator = carRepository.findAll();
        List<Car> allCar = new ArrayList<>();
        carIterator.forEachRemaining(allCar::add);
        return allCar;
    }

    @Override
    public Car findById(String carId) {
        // Mencari mobil spesifik berdasarkan ID
        Car car = carRepository.findById(carId);
        return car;
    }

    @Override
    public void update(String carId, Car car) {
        // Melakukan pembaruan data melalui repositori
        carRepository.update(carId, car);
    }

    @Override
    public void deleteCarById(String carId) {
        // Menghapus data mobil berdasarkan ID
        carRepository.delete(carId);
    }
}