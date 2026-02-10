package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @InjectMocks
    ProductRepository productRepository;
    @BeforeEach
    void setUp() {
    }

    @Test
    void testCreateAndFind() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);
        productRepository.create(product);

        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product savedProduct = productIterator.next();

        assertEquals(product.getProductId(), savedProduct.getProductId());
        assertEquals(product.getProductName(), savedProduct.getProductName());
        assertEquals(product.getProductQuantity(), savedProduct.getProductQuantity());
    }

    @Test
    void testFindAllIfEmpty() {
        Iterator<Product> productIterator = productRepository.findAll();
        assertFalse(productIterator.hasNext());
    }
    @Test
    void testFindAllIfMoreThanOneProduct() {
        Product product1 = new Product();
        product1.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product1.setProductName("Sampo Cap Bambang");
        product1.setProductQuantity(100);
        productRepository.create(product1);

        Product product2 = new Product();
        product2.setProductId("a0f9de46-90b1-437d-a0bf-d0821dde9096");
        product2.setProductName("Sampo Cap Usep");
        product2.setProductQuantity(50);
        productRepository.create(product2);

        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product savedProduct = productIterator.next();
        assertEquals(product1.getProductId(), savedProduct.getProductId());
        savedProduct = productIterator.next();
        assertEquals(product2.getProductId(), savedProduct.getProductId());
        assertFalse(productIterator.hasNext());
    }

    @Test
    void testEditProductPositive() {
        Product product = new Product();
        product.setProductId("id-123");
        product.setProductName("Product Lama");
        product.setProductQuantity(10);
        productRepository.create(product);

        Product updatedProduct = new Product();
        updatedProduct.setProductId("id-123");
        updatedProduct.setProductName("Product Baru");
        updatedProduct.setProductQuantity(20);
        Product result = productRepository.update(updatedProduct);

        assertNotNull(result);
        assertEquals("Product Baru", result.getProductName());
        assertEquals(20, result.getProductQuantity());

        Product storedProduct = productRepository.findById("id-123");
        assertEquals("Product Baru", storedProduct.getProductName());
    }

    @Test
    void testEditProductNegative() {
        Product updatedProduct = new Product();
        updatedProduct.setProductId("id-gaib");
        updatedProduct.setProductName("Product Gaib");
        updatedProduct.setProductQuantity(0);

        Product result = productRepository.update(updatedProduct);

        assertNull(result);
    }

    @Test
    void testDeleteProductPositive() {
        Product product = new Product();
        product.setProductId("id-delete");
        product.setProductName("Mau Dihapus");
        product.setProductQuantity(1);
        productRepository.create(product);

        productRepository.delete("id-delete");

        assertNull(productRepository.findById("id-delete"));
        Iterator<Product> iterator = productRepository.findAll();
        assertFalse(iterator.hasNext());
    }

    @Test
    void testDeleteProductNegative() {
        Product product = new Product();
        product.setProductId("id-tetap");
        product.setProductName("Jangan Dihapus");
        productRepository.create(product);

        productRepository.delete("id-salah");

        assertNotNull(productRepository.findById("id-tetap"));
        Iterator<Product> iterator = productRepository.findAll();
        assertTrue(iterator.hasNext());
    }
}
