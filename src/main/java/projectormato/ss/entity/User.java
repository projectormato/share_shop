package projectormato.ss.entity;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "email")
    private String email;

    public User(Long id, String userId, String email) {
        this.id = id;
        this.userId = userId;
        this.email = email;
    }

    public User() {
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getEmail() {
        return this.email;
    }

    public static class UserBuilder {
        private Long id;
        private String userId;
        private String email;

        UserBuilder() {
        }

        public User.UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public User.UserBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public User.UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public User build() {
            return new User(id, userId, email);
        }

        public String toString() {
            return "User.UserBuilder(id=" + this.id + ", userId=" + this.userId + ", email=" + this.email + ")";
        }
    }
}
