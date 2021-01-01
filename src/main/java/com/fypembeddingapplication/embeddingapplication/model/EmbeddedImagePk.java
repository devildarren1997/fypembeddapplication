package com.fypembeddingapplication.embeddingapplication.model;

import java.io.Serializable;
import java.util.Objects;




public class EmbeddedImagePk implements Serializable {
    private Long user_Id;
    private Long embedded_image_Id;

    public EmbeddedImagePk(Long user_Id, Long embedded_image_Id) {
        this.user_Id = user_Id;
        this.embedded_image_Id = embedded_image_Id;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        EmbeddedImagePk pk = (EmbeddedImagePk) o;
        return Objects.equals( user_Id, pk.user_Id ) &&
                Objects.equals( embedded_image_Id, pk.embedded_image_Id );
    }

    @Override
    public int hashCode() {
        return Objects.hash( user_Id, embedded_image_Id );
    }
}
