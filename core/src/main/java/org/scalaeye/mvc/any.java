package org.scalaeye.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于自定义route。如果value为默认值，表示仍然使用方法名作为action的名称。
 *
 * 用法示例：
 *
 * <pre>
 * <code>
 * class Users extends Controller("/users") {
 *
 *     @any("/aaa")
 *     def abc() {}
 *
 *     @any
 *     def sdf() {}
 * }
 * </code>
 * </pre>
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface any {

	public String value() default "";

}
