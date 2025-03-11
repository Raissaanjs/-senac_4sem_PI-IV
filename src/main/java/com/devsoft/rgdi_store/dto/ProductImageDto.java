package com.devsoft.rgdi_store.dto;

public class ProductImageDto {

    private Long id;
    private String url;
    private Long productId;

    public ProductImageDto() {}

    public ProductImageDto(Long id, String url, Long productId) {
        this.id = id;
        this.url = url;
        this.productId = productId;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
