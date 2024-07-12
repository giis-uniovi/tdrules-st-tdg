package javax.xml.bind.annotation;

public @interface XmlRootElement {
	String name() default "##default";
}
