package javax.xml.bind.annotation;

public @interface XmlElement {
	String name() default "##default";
}
