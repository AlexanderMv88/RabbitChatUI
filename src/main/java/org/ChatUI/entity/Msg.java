package org.ChatUI.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Data
@NoArgsConstructor
public class Msg {

    private long id;
    private String text;

    private Employee sender;

    public Msg(String text, Employee sender) {
        this.text = text;
        this.sender = sender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Msg msg = (Msg) o;
        return id == msg.id &&
                Objects.equals(text, msg.text);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, text);
    }
}
