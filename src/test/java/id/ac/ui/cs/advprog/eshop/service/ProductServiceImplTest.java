package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    private static final String PRODUCT_ID_2 = "product-2";

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createShouldCallRepositoryAndReturnProduct() {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);

        when(productRepository.create(product)).thenReturn(product);

        Product result = productService.create(product);

        verify(productRepository).create(product);
        assertSame(product, result);
    }

    @Test
    void findByIdShouldReturnProductFromRepository() {
        Product product = new Product();
        product.setProductId(PRODUCT_ID_2);
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);

        when(productRepository.findById(PRODUCT_ID_2)).thenReturn(product);

        Product result = productService.findById(PRODUCT_ID_2);

        verify(productRepository).findById(PRODUCT_ID_2);
        assertSame(product, result);
    }

    @Test
    void updateShouldCallRepositoryUpdate() {
        Product product = new Product();
        product.setProductId("product-3");
        product.setProductName("Sampo Baru");
        product.setProductQuantity(200);

        productService.update(product);

        verify(productRepository).update(product);
    }

    @Test
    void deleteShouldCallRepositoryDelete() {
        productService.delete("product-1");

        verify(productRepository).delete("product-1");
    }

    @Test
    void findAllShouldConvertIteratorToList() {
        Product firstProduct = new Product();
        firstProduct.setProductId("product-4");
        firstProduct.setProductName("Sampo Cap Bambang");
        firstProduct.setProductQuantity(100);

        Product secondProduct = new Product();
        secondProduct.setProductId("product-5");
        secondProduct.setProductName("Sampo Cap Usep");
        secondProduct.setProductQuantity(50);

        Iterator<Product> iterator = List.of(firstProduct, secondProduct).iterator();
        when(productRepository.findAll()).thenReturn(iterator);

        List<Product> result = productService.findAll();

        verify(productRepository).findAll();
        assertEquals(2, result.size());
        assertEquals("product-4", result.get(0).getProductId());
        assertEquals("product-5", result.get(1).getProductId());
    }

    @Test
    void findAllShouldReturnEmptyListWhenRepositoryIsEmpty() {
        when(productRepository.findAll()).thenReturn(List.<Product>of().iterator());

        List<Product> result = productService.findAll();

        verify(productRepository).findAll();
        assertTrue(result.isEmpty());
    }
}
