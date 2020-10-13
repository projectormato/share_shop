package projectormato.ss.entity;

import javax.persistence.*;

@Entity
@Table(name = "share_shared")
public class Share {
    @Id
    @Column(name = "share_shared_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "share_id")
    private String shareId;

    @Column(name = "shared_id")
    private String sharedId;

    public Share(Long id, String shareId, String sharedId) {
        this.id = id;
        this.shareId = shareId;
        this.sharedId = sharedId;
    }

    public Share() {
    }

    public static ShareBuilder builder() {
        return new ShareBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getShareId() {
        return this.shareId;
    }

    public String getSharedId() {
        return this.sharedId;
    }

    public static class ShareBuilder {
        private Long id;
        private String shareId;
        private String sharedId;

        ShareBuilder() {
        }

        public Share.ShareBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public Share.ShareBuilder shareId(String shareId) {
            this.shareId = shareId;
            return this;
        }

        public Share.ShareBuilder sharedId(String sharedId) {
            this.sharedId = sharedId;
            return this;
        }

        public Share build() {
            return new Share(id, shareId, sharedId);
        }

        public String toString() {
            return "Share.ShareBuilder(id=" + this.id + ", shareId=" + this.shareId + ", sharedId=" + this.sharedId + ")";
        }
    }
}
