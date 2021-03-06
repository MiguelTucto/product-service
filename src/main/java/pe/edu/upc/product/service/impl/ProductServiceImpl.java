package pe.edu.upc.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upc.product.entity.Category;
import pe.edu.upc.product.entity.Product;
import pe.edu.upc.product.repository.ProductRepository;
import pe.edu.upc.product.service.ProductService;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> listAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public Product getProduct(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product createProduct(Product product) {
        product.setStatus("CREATED");
        product.setCreateAt(new Date());
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
       Product productDB = getProduct(product.getId());
       if (productDB == null){
           return null;
       }
       productDB.setName(product.getName());
       productDB.setDescription(product.getDescription());
       productDB.setCategory(product.getCategory());
       productDB.setPrice(product.getPrice());
       return productRepository.save(productDB);
    }

    @Override
    public Product deleteProduct(Long id) {
        Product productDb = getProduct(id);
        if(productDb == null) {
            return null;
        }
        productDb.setStatus("DELETED");
        return productRepository.save(productDb);
    }

    @Override
    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public Product updateStock(Long id, Double quantity) {
        Product productDb = getProduct(id);
        if(productDb == null){
            return null;
        }
        Double stock = productDb.getStock() + quantity;
        productDb.setStock(stock);

        return productRepository.save(productDb);
    }
}
