package com.example.diploma.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Base64;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idimage", nullable = false)
    private Long idimage;
    @Lob
    private String bytes;

    public String decoder(String encoded)
    {
        Base64.Decoder decoder = Base64.getDecoder();
        String decoded = new String(decoder.decode(encoded));
        return decoded;
    }
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Product product;
    @Basic
    @Column(name = "content_type", nullable = true, length = 255)
    private String contentType;
    @Basic
    @Column(name = "is_preview_image", nullable = true)
    private Boolean isPreviewImage;
    @Basic
    @Column(name = "name", nullable = true, length = 255)
    private String name;
    @Basic
    @Column(name = "original_file_name", nullable = true, length = 255)
    private String originalFileName;
    @Basic
    @Column(name = "size", nullable = true)
    private Long size;

}
