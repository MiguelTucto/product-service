package pe.edu.upc.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upc.product.entity.Product;
import pe.edu.upc.product.entity.Category;
import pe.edu.upc.product.service.ProductService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    // -- recuperar todos los clientes --

    @GetMapping
    public ResponseEntity<List<Product>> listProducts(@RequestParam(name = "categoryId", required = false) Long categoryId) {
        List<Product> products = new ArrayList<>();

        if (categoryId == null) {
            products = productService.listAllProduct();
            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
        } else {
           products = productService.findByCategory(Category.builder().id(categoryId).build());
           if (products == null){
               return ResponseEntity.notFound().build();
           }
        }
        return ResponseEntity.ok(products);
    }

    // -- recuperar un unico product --
    @GetMapping(value = "{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") Long id) {
        log.info("Getting Product ID {}", id);
        Product product = productService.getProduct(id);
        if (product == null ) {
            log.error("This Product ID {} doesn't found");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    // -- crear product --
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product, BindingResult result) {
        log.info("Creating Product {}", product);
        if (result.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, this.formatMessage(result));
        }

        Product productDB = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDB);
    }

    // --actualizar product --
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") long id, @RequestBody Product product) {
        product.setId(id);
        Product currentProduct = productService.updateProduct(product);

        if (currentProduct == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(currentProduct);
    }

    // --eliminar product --
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {
        Product productDelete = productService.deleteProduct(id);
        if (productDelete == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productDelete);
    }

    @PutMapping(value = "/{id}/stock")
    public ResponseEntity<Product> updateStockProduct(@PathVariable Long id, @RequestParam(name = "quantity", required = false) Double quantity){
        Product product = productService.updateStock(id, quantity);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }
    private String formatMessage(BindingResult result)
    {
        List<Map<String, String>> errors = result.getFieldErrors().stream()
                .map(err ->{
                    Map<String, String> error = new HashMap<>();
                    error.put(err.getField(), err.getDefaultMessage());
                    return error;
                }).collect(Collectors.toList());
        ErrorMessage errorMessage = ErrorMessage.builder()
                .code("01")
                .messages(errors).build();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(errorMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
