package ru.netology.nework.entity

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.netology.nework.dto.User
import javax.persistence.*

@Entity
data class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long,
    @Column(unique = true)
    var login: String,
    var pass: String,
    var name: String,
    var avatar: String,
) : UserDetails {
    constructor(id: Long) : this(id, "", "", "", "")

    override fun getUsername(): String = login
    override fun getPassword(): String = pass
    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_USER"))
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    fun toDto() = User(id, login, name, avatar, authorities.map(GrantedAuthority::getAuthority))
}