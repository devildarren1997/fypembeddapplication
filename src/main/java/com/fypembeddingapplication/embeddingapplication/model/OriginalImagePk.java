package com.fypembeddingapplication.embeddingapplication.model;

import java.io.Serializable;
import java.util.Objects;

public class OriginalImagePk implements Serializable {
    private Long user_Id;
    private Long original_image_Id;

    public OriginalImagePk(Long user_Id, Long original_image_Id) {
        this.user_Id = user_Id;
        this.original_image_Id = original_image_Id;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        OriginalImagePk pk = (OriginalImagePk) o;
        return Objects.equals( user_Id, pk.user_Id ) &&
                Objects.equals( original_image_Id, pk.original_image_Id );
    }

    @Override
    public int hashCode() {
        return Objects.hash( user_Id, original_image_Id );
    }
}
