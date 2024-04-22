package com.example.service1.resolver;

public abstract class Resolver {

    public abstract Object resolve(Object value);

    public Object resolve(Object value, Object ref) {
        return ref == null ? this.resolve(value) : this.resolve(value, ref);
    }

    public Resolver() {

    }

}
