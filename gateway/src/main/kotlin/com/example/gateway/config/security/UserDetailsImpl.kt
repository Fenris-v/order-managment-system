// todo
//package com.example.gateway.config.security
//
//import com.example.gateway.dto.UserDto
//import org.springframework.security.core.GrantedAuthority
//import org.springframework.security.core.authority.SimpleGrantedAuthority
//import org.springframework.security.core.userdetails.UserDetails
//
//class UserDetailsImpl(private val userDto: UserDto) : UserDetails {
//    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
//        return mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
//    }
//
//    override fun getPassword(): String {
//        return userDto.password
//    }
//
//    override fun getUsername(): String {
//        return userDto.login
//    }
//
//    override fun isAccountNonExpired(): Boolean {
//        return true
//    }
//
//    override fun isAccountNonLocked(): Boolean {
//        return true
//    }
//
//    override fun isCredentialsNonExpired(): Boolean {
//        return true
//    }
//
//    override fun isEnabled(): Boolean {
//        return true
//    }
//}
