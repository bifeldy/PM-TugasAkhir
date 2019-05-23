package id.ac.umn.pm_tugasakhir_v2;

public class Product {
    private int productId;
    private String productName;
    private String productImage;
    private int productPrice;
    private int productStock;
    private String productDescription;
    private String productCategory;
    private int productFavorite;

    public Product(){}

    public Product(int productId, String productName, String productImage, String productDescription, String productCategory, int productStock, int productPrice, int productFavorite) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productStock = productStock;
        this.productPrice = productPrice;
        this.productFavorite = productFavorite;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public int getProductStock() {
        return productStock;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public int getProductFavorite() {
        return productFavorite;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setProductStock(int productStock) {
        this.productStock = productStock;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductFavorite(int productFavorite) {
        this.productFavorite = productFavorite;
    }
}
