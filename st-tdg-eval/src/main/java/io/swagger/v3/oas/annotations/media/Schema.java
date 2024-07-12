package io.swagger.v3.oas.annotations.media;

public @interface Schema {
	String description() default "";
	String allowableValues() default "";
}
