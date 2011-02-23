// 如果model比较少，就直接与在这里，多的话，最好放在单独的文件里
package models

// 此处借用circumflex-orm的api
class User extends Record[Long,User] with IdentityGenerator[Long, User] {
	val id = "id".bigint.notNull.autoIncrement // 主键
	val email = "email".text.notNull
	val password = "password".text.notNull
	val createdAt = "created_at".timestamp.notNull(new Date)

	def relation = User
	def PRIMARY_ID = id
}

object User extends User with Table[Long, User] {
	override def qualifiedName = "users"
}
