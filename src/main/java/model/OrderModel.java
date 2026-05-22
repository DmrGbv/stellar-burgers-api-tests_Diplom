package model;

import lombok.Data;

import java.util.List;

@Data

public class OrderModel {
    private List<String> ingredients;

    public OrderModel(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}