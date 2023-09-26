package it.Services.CarPark.Exceptions;

public class CarParkNotFoundException extends RuntimeException {
    public CarParkNotFoundException(String message) {
        super(message);
    }
}
