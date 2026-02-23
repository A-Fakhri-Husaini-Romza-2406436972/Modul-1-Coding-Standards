package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void createProductPageShouldShowCreateProductViewWithEmptyProduct() throws Exception {
        mockMvc.perform(get("/product/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("CreateProduct"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    void createProductPostShouldCreateProductAndRedirectToList() throws Exception {
        when(productService.create(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/product/create")
                        .param("productId", "product-1")
                        .param("productName", "Sampo Cap Bambang")
                        .param("productQuantity", "100"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:list"));

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService).create(productCaptor.capture());
        Product createdProduct = productCaptor.getValue();
        assertEquals("product-1", createdProduct.getProductId());
        assertEquals("Sampo Cap Bambang", createdProduct.getProductName());
        assertEquals(100, createdProduct.getProductQuantity());
    }

    @Test
    void editProductPageShouldShowEditProductViewWithExistingProduct() throws Exception {
        Product existingProduct = new Product();
        existingProduct.setProductId("product-1");
        existingProduct.setProductName("Sampo Cap Bambang");
        existingProduct.setProductQuantity(100);

        when(productService.findById("product-1")).thenReturn(existingProduct);

        mockMvc.perform(get("/product/edit/product-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("EditProduct"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attribute("product", samePropertyValuesAs(existingProduct)));
    }

    @Test
    void editProductPostShouldUpdateProductAndRedirectToList() throws Exception {
        mockMvc.perform(post("/product/edit")
                        .param("productId", "product-1")
                        .param("productName", "Sampo Baru")
                        .param("productQuantity", "200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:list"));

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService).update(productCaptor.capture());
        Product updatedProduct = productCaptor.getValue();
        assertEquals("product-1", updatedProduct.getProductId());
        assertEquals("Sampo Baru", updatedProduct.getProductName());
        assertEquals(200, updatedProduct.getProductQuantity());
    }

    @Test
    void deleteProductShouldDeleteProductAndRedirectToList() throws Exception {
        mockMvc.perform(get("/product/delete/product-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:../list"));

        verify(productService).delete("product-1");
    }

    @Test
    void productListPageShouldShowAllProducts() throws Exception {
        Product firstProduct = new Product();
        firstProduct.setProductId("product-1");
        firstProduct.setProductName("Sampo Cap Bambang");
        firstProduct.setProductQuantity(100);

        Product secondProduct = new Product();
        secondProduct.setProductId("product-2");
        secondProduct.setProductName("Sampo Cap Usep");
        secondProduct.setProductQuantity(50);

        when(productService.findAll()).thenReturn(List.of(firstProduct, secondProduct));

        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("ProductList"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", hasSize(2)));
    }
}
