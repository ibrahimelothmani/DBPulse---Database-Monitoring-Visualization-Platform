package com.ibrahim.DBPulse.repositories;

import com.ibrahim.DBPulse.IntegrationTestBase;
import com.ibrahim.DBPulse.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for ProductRepository.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest extends IntegrationTestBase {

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("A great product");
        testProduct.setSku("TEST-001");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStockQuantity(100);
        testProduct.setCategory("Electronics");
        testProduct.setActive(true);
    }

    @Test
    @DisplayName("Should save product successfully")
    void testSaveProduct() {
        // When
        Product savedProduct = productRepository.save(testProduct);

        // Then
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        assertThat(savedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(savedProduct.isInStock()).isTrue();
    }

    @Test
    @DisplayName("Should find product by SKU")
    void testFindBySku() {
        // Given
        productRepository.save(testProduct);

        // When
        Optional<Product> found = productRepository.findBySku("TEST-001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("Should check if SKU exists")
    void testExistsBySku() {
        // Given
        productRepository.save(testProduct);

        // When
        boolean exists = productRepository.existsBySku("TEST-001");
        boolean notExists = productRepository.existsBySku("OTHER-SKU");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find products by category")
    void testFindByCategory() {
        // Given
        productRepository.save(testProduct);

        Product clothingProduct = new Product();
        clothingProduct.setName("T-Shirt");
        clothingProduct.setSku("SHIRT-001");
        clothingProduct.setPrice(new BigDecimal("29.99"));
        clothingProduct.setCategory("Clothing");
        productRepository.save(clothingProduct);

        // When
        List<Product> electronics = productRepository.findByCategory("Electronics");

        // Then
        assertThat(electronics).hasSize(1);
        assertThat(electronics.get(0).getName()).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("Should find products in stock")
    void testFindInStockProducts() {
        // Given
        productRepository.save(testProduct);

        Product outOfStock = new Product();
        outOfStock.setName("Out of Stock Product");
        outOfStock.setSku("OOS-001");
        outOfStock.setPrice(new BigDecimal("49.99"));
        outOfStock.setStockQuantity(0);
        outOfStock.setActive(true);
        productRepository.save(outOfStock);

        // When
        List<Product> inStock = productRepository.findInStockProducts();

        // Then
        assertThat(inStock).hasSize(1);
        assertThat(inStock.get(0).getSku()).isEqualTo("TEST-001");
    }

    @Test
    @DisplayName("Should find products by price range")
    void testFindByPriceBetween() {
        // Given
        productRepository.save(testProduct);

        Product cheapProduct = new Product();
        cheapProduct.setName("Cheap Product");
        cheapProduct.setSku("CHEAP-001");
        cheapProduct.setPrice(new BigDecimal("9.99"));
        cheapProduct.setStockQuantity(50);
        productRepository.save(cheapProduct);

        // When
        List<Product> midRange = productRepository.findByPriceBetween(
                new BigDecimal("50.00"),
                new BigDecimal("150.00"));

        // Then
        assertThat(midRange).hasSize(1);
        assertThat(midRange.get(0).getName()).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("Should find low stock products")
    void testFindLowStockProducts() {
        // Given
        productRepository.save(testProduct);

        Product lowStock = new Product();
        lowStock.setName("Low Stock Product");
        lowStock.setSku("LOW-001");
        lowStock.setPrice(new BigDecimal("29.99"));
        lowStock.setStockQuantity(3);
        lowStock.setActive(true);
        productRepository.save(lowStock);

        // When
        List<Product> lowStockProducts = productRepository.findLowStockProducts(10);

        // Then
        assertThat(lowStockProducts).hasSize(1);
        assertThat(lowStockProducts.get(0).getSku()).isEqualTo("LOW-001");
    }

    @Test
    @DisplayName("Should count products by category")
    void testCountByCategory() {
        // Given
        productRepository.save(testProduct);

        Product anotherElectronic = new Product();
        anotherElectronic.setName("Laptop");
        anotherElectronic.setSku("LAPTOP-001");
        anotherElectronic.setPrice(new BigDecimal("999.99"));
        anotherElectronic.setCategory("Electronics");
        productRepository.save(anotherElectronic);

        // When
        long count = productRepository.countByCategory("Electronics");

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find all unique categories")
    void testFindAllCategories() {
        // Given
        productRepository.save(testProduct);

        Product clothingProduct = new Product();
        clothingProduct.setName("T-Shirt");
        clothingProduct.setSku("SHIRT-001");
        clothingProduct.setPrice(new BigDecimal("29.99"));
        clothingProduct.setCategory("Clothing");
        productRepository.save(clothingProduct);

        // When
        List<String> categories = productRepository.findAllCategories();

        // Then
        assertThat(categories).hasSize(2);
        assertThat(categories).containsExactlyInAnyOrder("Electronics", "Clothing");
    }
}
