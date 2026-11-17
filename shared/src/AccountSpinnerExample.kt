package com.example.aip_les_9

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aip_les_9.icons.AppIcons
import com.example.aip_les_9.icons.ArrowDropDown
import com.example.aip_les_9.icons.Email
import com.example.aip_les_9.icons.Phone

sealed interface Account {
    val name: String

    data class Email(
        val email: String,
        override val name: String,
        val profileImageUrl: String? = null,
        val lastLoginDate: String? = null
    ) : Account {
        override fun toString(): String {
            return "$name \n $email"
        }
    }

    data class Phone(
        val phoneNumber: String,
        override val name: String,
        val countryCode: String? = null,
        val isTwoFactorEnabled: Boolean = false
    ) : Account {
        override fun toString(): String {
            return "$name \n $phoneNumber"
        }
    }
}

@Composable
fun EmailAccountItem(
    account: Account.Email,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = AppIcons.Email,
            contentDescription = "Email Account",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = account.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = account.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PhoneAccountItem(
    account: Account.Phone,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = AppIcons.Phone,
            contentDescription = "Phone Account",
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = account.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = account.phoneNumber,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AccountItem(
    account: Account,
    modifier: Modifier = Modifier
) {
    when (account) {
        is Account.Email -> EmailAccountItem(account = account, modifier = modifier)
        is Account.Phone -> PhoneAccountItem(account = account, modifier = modifier)
    }
}

@Composable
fun <T> AccountSpinner(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemLabel: (T) -> String = { it?.toString() ?: "" },
    itemContent: @Composable (T) -> Unit = { Text(itemLabel(it)) }
) {
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = modifier) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    modifier = Modifier.padding(4.dp),
                    text = {
                        itemContent(item)
                    }
                )
            }
        }
        // Text field with dropdown arrow
        OutlinedTextField(
            value = selectedItem?.let(itemLabel) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Account") },
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    Icon(AppIcons.ArrowDropDown, contentDescription = null)
                }
            }
        )
    }
}

@Composable
fun AccountSpinnerExample(
    modifier: Modifier = Modifier
) {
    var selectedAccount by remember { mutableStateOf<Account?>(null) }

    val accounts = remember {
        listOf<Account>(
            Account.Email("john.doe@email.com", "John Doe"),
            Account.Email("jane.smith@email.com", "Jane Smith"),
            Account.Phone("123-456-7890", "John Doe"),
            Account.Phone("987-654-3210", "Jane Smith")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AccountSpinner(
            items = accounts,
            selectedItem = selectedAccount,
            onItemSelected = { selectedAccount = it },
            itemLabel = { account ->
                when (account) {
                    is Account.Email -> "${account.name} (${account.email})"
                    is Account.Phone -> "${account.name} (${account.phoneNumber})"
                }
            },
            itemContent = { account ->
                AccountItem(account = account)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display selected account information
        selectedAccount?.let { account ->
            Text(
                text = "Selected Account:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            AccountItem(account = account)
        }
    }
}
