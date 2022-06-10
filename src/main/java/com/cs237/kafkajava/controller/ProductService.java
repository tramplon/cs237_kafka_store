package com.cs237.kafkajava.controller;

import java.util.List;

public interface ProductService {
    public List<Shoes> getAllProducts();

    public void updateProduct(String id);

    public void insertProduct(Shoes shoe);
}
