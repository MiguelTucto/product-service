package pe.edu.upc.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.product.entity.Category;
import pe.edu.upc.product.entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    public List<Product> findByCategory(Category category);

}
