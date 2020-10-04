package projectormato.ss.entity;

import javax.persistence.*;

// TODO: Kotlin化
@Entity
@Table(name = "shop")
public class Shop {
    @Id
    @Column(name = "shop_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "url")
    private String url;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "hours")
    private String hours;

    public Shop(Long id, String userId, String url, String name, String address, String hours) {
        this.id = id;
        this.userId = userId;
        this.url = url;
        this.name = name;
        this.address = address;
        this.hours = hours;
    }

    public Shop() {
    }

    public static ShopBuilder builder() {
        return new ShopBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getUrl() {
        return this.url;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public String getHours() {
        return this.hours;
    }

    public static class ShopBuilder {
        private Long id;
        private String userId;
        private String url;
        private String name;
        private String address;
        private String hours;

        ShopBuilder() {
        }

        public Shop.ShopBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public Shop.ShopBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Shop.ShopBuilder url(String url) {
            this.url = url;
            return this;
        }

        public Shop.ShopBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Shop.ShopBuilder address(String address) {
            this.address = address;
            return this;
        }

        public Shop.ShopBuilder hours(String hours) {
            this.hours = hours;
            return this;
        }

        public Shop build() {
            return new Shop(id, userId, url, name, address, hours);
        }

        public String toString() {
            return "Shop.ShopBuilder(id=" + this.id + ", userId=" + this.userId + ", url=" + this.url + ", name=" + this.name + ", address=" + this.address + ", hours=" + this.hours + ")";
        }
    }
}
