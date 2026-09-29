package io.github.chandu4221.designode.domain.spec

enum class AtomicLevel {
    ATOM,       // Text, Icon, Spacer
    MOLECULE,   // Button, Card, ListItem
    ORGANISM,   // Scaffold, TopAppBar, NavigationBar
    TEMPLATE,   // root-level layout containers
}