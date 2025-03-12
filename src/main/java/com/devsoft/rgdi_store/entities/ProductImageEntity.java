package com.devsoft.rgdi_store.entities;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_produtoimagem")
public class ProductImageEntity {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    private String url;

	    @ManyToOne
	    @JoinColumn(name = "product_id")
	    private ProductEntity product;

	    // Construtor padrão
	    public ProductImageEntity() {}

		public ProductImageEntity(Long id, String url, ProductEntity product) {
			super();
			this.id = id;
			this.url = url;
			this.product = product;
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
		
		public ProductEntity getProductEntity() {
			return product;
		}

		public void setProductEntity(ProductEntity product) {
			this.product = product;
		}

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ProductImageEntity other = (ProductImageEntity) obj;
			return Objects.equals(id, other.id);
		}

}
