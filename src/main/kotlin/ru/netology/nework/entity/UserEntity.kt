package ru.netology.nework.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.netology.nework.dto.User
import ru.netology.nework.dto.UserPreview
import ru.netology.nework.dto.UserResponse

@Entity
data class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long,
    @Column(unique = true)
    val login: String,
    val pass: String,
    val name: String,
    val avatar: String? = null,
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

    fun toResponse() = UserResponse(id, login, name, avatar)

    fun toPreview() = UserPreview(name = name, avatar = avatar)
}