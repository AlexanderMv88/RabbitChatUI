package org.ChatUI.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Employee {

    private long id;
    private String fullName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employee employee = (Employee) o;

        if (id != employee.id) return false;
        return fullName != null ? fullName.equals(employee.fullName) : employee.fullName == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        return result;
    }

    public Employee(String fullName) {
        this.fullName = fullName;
    }

    public Employee(Employee employee) {
        this.id = employee.getId();
        this.fullName=employee.getFullName();
    }

    @Override
    public String toString() {
        return fullName;
    }
}
