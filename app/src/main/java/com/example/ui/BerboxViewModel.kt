package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BerboxRepository
import com.example.data.CartItem
import com.example.data.local.AppDatabase
import com.example.data.local.CompanyEntity
import com.example.data.local.CustomerEntity
import com.example.data.local.DisbursementEntity
import com.example.data.local.ProductEntity
import com.example.data.local.ReceiptEntity
import com.example.data.local.SaleEntity
import com.example.data.local.SalesReturnEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    DASHBOARD,
    SALES,
    PRODUCTS,
    REPORTS,
    MORE
}

enum class MoreOption {
    CUSTOMERS,
    RECEIPTS,
    DISBURSEMENTS,
    ACCOUNT_STATEMENT,
    SUBSCRIPTIONS,
    ACCOUNT_SETTINGS,
    COMMISSION,
    SALES,
    REPORTS
}

enum class OnboardingStep {
    INITIAL_SPLASH,
    SPLASH_SWIPER,
    LOGIN,
    MANDATORY_FORM,
    COMPLETED
}

class BerboxViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val repository = BerboxRepository(db)

    val company: StateFlow<CompanyEntity?> = repository.company.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val products: StateFlow<List<ProductEntity>> = repository.products.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val lowStockProducts: StateFlow<List<ProductEntity>> = repository.lowStockProducts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val customers: StateFlow<List<CustomerEntity>> = repository.customers.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val sales: StateFlow<List<SaleEntity>> = repository.sales.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val receipts: StateFlow<List<ReceiptEntity>> = repository.receipts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val disbursements: StateFlow<List<DisbursementEntity>> = repository.disbursements.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _currentTab = MutableStateFlow(AppTab.DASHBOARD)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _activeMoreOption = MutableStateFlow<MoreOption?>(null)
    val activeMoreOption: StateFlow<MoreOption?> = _activeMoreOption.asStateFlow()

    private val _onboardingStep = MutableStateFlow(OnboardingStep.INITIAL_SPLASH)
    val onboardingStep: StateFlow<OnboardingStep> = _onboardingStep.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _isNewSaleSheetOpen = MutableStateFlow(false)
    val isNewSaleSheetOpen: StateFlow<Boolean> = _isNewSaleSheetOpen.asStateFlow()

    private val _isAddProductDialogOpen = MutableStateFlow(false)
    val isAddProductDialogOpen: StateFlow<Boolean> = _isAddProductDialogOpen.asStateFlow()

    private val _editingProduct = MutableStateFlow<ProductEntity?>(null)
    val editingProduct: StateFlow<ProductEntity?> = _editingProduct.asStateFlow()

    private val _isAddCustomerDialogOpen = MutableStateFlow(false)
    val isAddCustomerDialogOpen: StateFlow<Boolean> = _isAddCustomerDialogOpen.asStateFlow()

    private val _editingCustomer = MutableStateFlow<CustomerEntity?>(null)
    val editingCustomer: StateFlow<CustomerEntity?> = _editingCustomer.asStateFlow()

    private val _isAddReceiptDialogOpen = MutableStateFlow(false)
    val isAddReceiptDialogOpen: StateFlow<Boolean> = _isAddReceiptDialogOpen.asStateFlow()

    private val _isAddDisbursementDialogOpen = MutableStateFlow(false)
    val isAddDisbursementDialogOpen: StateFlow<Boolean> = _isAddDisbursementDialogOpen.asStateFlow()

    private val _selectedStatementCustomer = MutableStateFlow("عميل عام")
    val selectedStatementCustomer: StateFlow<String> = _selectedStatementCustomer.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    private val _showWelcomeGift = MutableStateFlow(false)
    val showWelcomeGift: StateFlow<Boolean> = _showWelcomeGift.asStateFlow()

    fun dismissWelcomeGift() { _showWelcomeGift.value = false }

    init {
        viewModelScope.launch {
            // repository.seedSampleDataIfEmpty()
            val existingCompany = repository.getCompanyOnce()
            if (existingCompany != null) {
                _onboardingStep.value = OnboardingStep.COMPLETED
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
        if (tab != AppTab.MORE) {
            _activeMoreOption.value = null
        }
    }

    fun selectMoreOption(option: MoreOption) {
        _currentTab.value = AppTab.MORE
        _activeMoreOption.value = option
    }

    fun clearMoreOption() {
        _activeMoreOption.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun updateProfile(
        companyName: String,
        repName: String,
        repPhone: String,
        locationText: String
    ) {
        viewModelScope.launch {
            val existing = company.value
            if (existing != null) {
                val updated = existing.copy(
                    companyName = companyName,
                    repName = repName,
                    repPhone = repPhone,
                    locationText = locationText
                )
                repository.saveCompany(updated)
                _userMessage.value = "تم تحديث الملف الشخصي بنجاح"
            }
        }
    }

    // Onboarding Actions
    fun finishInitialSplash() {
        _onboardingStep.value = OnboardingStep.SPLASH_SWIPER
    }

    fun skipSwiperToLogin() {
        _onboardingStep.value = OnboardingStep.LOGIN
    }

    private val _tempGoogleName = MutableStateFlow("")
    val tempGoogleName: StateFlow<String> = _tempGoogleName.asStateFlow()

    private val _tempGoogleEmail = MutableStateFlow("")
    val tempGoogleEmail: StateFlow<String> = _tempGoogleEmail.asStateFlow()

    fun performPhoneLogin(phone: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _userMessage.value = null
            var existing = repository.getCompanyOnce()
            
            if (existing == null) {
                // Try fetching from Supabase
                val remoteCompany = repository.fetchCompanyFromSupabase(phone)
                if (remoteCompany != null) {
                    repository.saveCompanyLocally(remoteCompany)
                    existing = remoteCompany
                }
            }
            
            if (existing == null) {
                _userMessage.value = "الحساب غير موجود. يرجى إنشاء حساب جديد."
            } else {
                if (existing.repPhone == phone && existing.password == pass) {
                    autoRestoreOnLogin(existing)

                    _onboardingStep.value = OnboardingStep.COMPLETED
                } else if (existing.password.isBlank()) {
                    autoRestoreOnLogin(existing)

                    // Fallback for older accounts without password
                    _onboardingStep.value = OnboardingStep.COMPLETED
                } else {
                    _userMessage.value = "بيانات الدخول غير صحيحة"
                }
            }
            _isLoading.value = false
        }
    }

    
    fun performGoogleLogin(email: String, name: String) {
        // If we already have a company, just log them in
        viewModelScope.launch {
            val existing = repository.getCompanyOnce()
            if (existing != null) {
                _onboardingStep.value = OnboardingStep.COMPLETED
            } else {
                // Otherwise, send them to the mandatory step-by-step form
                _tempGoogleName.value = name
                _onboardingStep.value = OnboardingStep.MANDATORY_FORM
            }
        }
    }

    fun navigateToCreateAccount() {
        _tempGoogleName.value = ""
        _onboardingStep.value = OnboardingStep.MANDATORY_FORM
    }

    fun submitMandatoryOnboarding(
        companyName: String,
        repName: String,
        repPhone: String,
        password: String,
        locationText: String,
        lat: Double,
        lng: Double
    ) {
        viewModelScope.launch {
            val newCompany = CompanyEntity(
                companyName = companyName,
                repName = repName,
                repPhone = repPhone,
                password = password,
                locationText = locationText,
                locationLat = lat,
                locationLng = lng,
                trialEndsAt = System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000L),
                subscriptionPlan = "trial",
                subscriptionExpiresAt = System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000L)
            )
            repository.saveCompany(newCompany)
            _showWelcomeGift.value = true
            _onboardingStep.value = OnboardingStep.COMPLETED
        }
    }

    // Product CRUD
    fun openAddProductDialog(product: ProductEntity? = null) {
        _editingProduct.value = product
        _isAddProductDialogOpen.value = true
    }

    fun closeAddProductDialog() {
        _isAddProductDialogOpen.value = false
        _editingProduct.value = null
    }

    fun saveProduct(
        name: String,
        description: String,
        barcode: String = "",
        quantity: Int,
        price: Double,
        costPrice: Double,
        lowStockThreshold: Int
    ) {
        viewModelScope.launch {
            val existing = _editingProduct.value
            if (existing != null) {
                repository.updateProduct(
                    existing.copy(
                        name = name,
                        description = description,
                        barcode = barcode,
                        quantity = quantity,
                        price = price,
                        costPrice = costPrice,
                        lowStockThreshold = lowStockThreshold
                    )
                )
            } else {
                repository.addProduct(
                    ProductEntity(
                        name = name,
                        description = description,
                        barcode = barcode,
                        quantity = quantity,
                        price = price,
                        costPrice = costPrice,
                        lowStockThreshold = lowStockThreshold
                    )
                )
            }
            closeAddProductDialog()
        }
    }

    fun scanAndAddToCart(scannedCode: String): Boolean {
        val trimmed = scannedCode.trim()
        val match = products.value.find { 
            (it.barcode.isNotBlank() && it.barcode.equals(trimmed, ignoreCase = true)) || 
            it.name.contains(trimmed, ignoreCase = true) || 
            it.id.toString() == trimmed
        }
        return if (match != null) {
            addProductToCart(match)
            _userMessage.value = "تمت إضافة '${match.name}' إلى السلة"
            true
        } else {
            _userMessage.value = "لم يتم العثور على منتج بالرمز: $trimmed"
            false
        }
    }

    fun deleteProduct(productId: Int) {
        autoSyncIfPro()
        viewModelScope.launch {
            val success = repository.deleteProduct(productId)
            if (!success) {
                _userMessage.value = "لا يمكن حذف المنتج لأنه مرتبط بمبيعات سابقة!"
            }
        }
    }

    // Cart & Sales Actions
    fun openNewSaleSheet() {
        _cartItems.value = emptyList()
        _isNewSaleSheetOpen.value = true
    }

    fun closeNewSaleSheet() {
        _isNewSaleSheetOpen.value = false
        _cartItems.value = emptyList()
    }

    fun addProductToCart(product: ProductEntity) {
        addProductToCartWithQty(product, 1, product.price)
    }

    fun addProductToCartWithQty(product: ProductEntity, qty: Int, price: Double = product.price) {
        if (qty <= 0) return
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val existing = currentList[index]
            currentList[index] = existing.copy(
                quantity = existing.quantity + qty,
                salePrice = price
            )
        } else {
            currentList.add(CartItem(product = product, quantity = qty, salePrice = price))
        }
        _cartItems.value = currentList
    }

    fun updateCartItemQuantity(productId: Int, delta: Int) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val existing = currentList[index]
            val newQty = existing.quantity + delta
            if (newQty <= 0) {
                currentList.removeAt(index)
            } else {
                currentList[index] = existing.copy(quantity = newQty)
            }
            _cartItems.value = currentList
        }
    }

    fun updateCartItemPrice(productId: Int, newPrice: Double) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            currentList[index] = currentList[index].copy(salePrice = newPrice)
            _cartItems.value = currentList
        }
    }

    fun confirmSale(customerName: String, status: String) {
        val cart = _cartItems.value
        if (cart.isEmpty()) return
        val finalCustomer = customerName.ifBlank { "عميل عام" }
        viewModelScope.launch {
            repository.createSale(cart, finalCustomer, status)
            closeNewSaleSheet()
            _userMessage.value = "تم تسجيل عملية البيع بنجاح!"
        }
    }

    fun deleteInvoice(transactionId: String) {
        autoSyncIfPro()
        viewModelScope.launch {
            repository.deleteInvoice(transactionId)
            _userMessage.value = "تم إلغاء الفاتورة وإعادة الكميات للمخزون"
        }
    }

    fun processReturn(saleItem: SaleEntity, returnQty: Int, reason: String) {
        autoSyncIfPro()
        viewModelScope.launch {
            repository.processReturn(saleItem, returnQty, reason)
            _userMessage.value = "تم تسجيل إرجاع $returnQty قطعة بنجاح"
        }
    }

    // Customer CRUD
    fun openAddCustomerDialog(customer: CustomerEntity? = null) {
        _editingCustomer.value = customer
        _isAddCustomerDialogOpen.value = true
    }

    fun closeAddCustomerDialog() {
        _isAddCustomerDialogOpen.value = false
        _editingCustomer.value = null
    }

    fun saveCustomer(name: String, phone: String, location: String) {
        viewModelScope.launch {
            val existing = _editingCustomer.value
            if (existing != null) {
                repository.updateCustomer(existing.copy(name = name, phone = phone, location = location))
            } else {
                repository.addCustomer(CustomerEntity(name = name, phone = phone, location = location))
            }
            closeAddCustomerDialog()
        }
    }

    fun deleteCustomer(id: Int) {
        autoSyncIfPro()
        viewModelScope.launch {
            repository.deleteCustomer(id)
        }
    }

    fun importContacts(contacts: List<Pair<String, String>>) {
        viewModelScope.launch {
            val existing = customers.value
            val existingPhones = existing.map { it.phone.replace("\\s+".toRegex(), "") }.toSet()
            val existingNames = existing.map { it.name.trim().lowercase() }.toSet()

            var count = 0
            contacts.forEach { (name, phone) ->
                val cleanPhone = phone.replace("\\s+".toRegex(), "")
                val cleanName = name.trim()
                if (cleanName.isNotBlank() && !existingPhones.contains(cleanPhone) && !existingNames.contains(cleanName.lowercase())) {
                    repository.addCustomer(CustomerEntity(name = cleanName, phone = phone))
                    count++
                }
            }
            if (count > 0) {
                _userMessage.value = "تم استيراد $count عميل من جهات الاتصال بنجاح!"
            } else {
                _userMessage.value = "لم يتم إضافة جهات اتصال جديدة (قد تكون مضافة مسبقاً)"
            }
        }
    }

    // Receipt CRUD
    fun openAddReceiptDialog() {
        _isAddReceiptDialogOpen.value = true
    }

    fun closeAddReceiptDialog() {
        _isAddReceiptDialogOpen.value = false
    }

    fun saveReceipt(customerName: String, amount: Double, notes: String) {
        viewModelScope.launch {
            val receiptNum = repository.createReceipt(customerName, amount, notes)
            closeAddReceiptDialog()
            _userMessage.value = "تم حفظ سند القبض رقم #$receiptNum بنجاح"
        }
    }

    // Disbursement CRUD
    fun openAddDisbursementDialog() {
        _isAddDisbursementDialogOpen.value = true
    }

    fun closeAddDisbursementDialog() {
        _isAddDisbursementDialogOpen.value = false
    }

    fun saveDisbursement(purpose: String, amount: Double, notes: String) {
        viewModelScope.launch {
            repository.createDisbursement(purpose, amount, notes)
            closeAddDisbursementDialog()
            _userMessage.value = "تم حفظ سند الصرف بنجاح"
        }
    }

    fun setSelectedStatementCustomer(name: String) {
        _selectedStatementCustomer.value = name
    }

    fun redeemActivationCode(code: String) {
        viewModelScope.launch {
            val company = repository.getCompanyOnce() ?: return@launch
            _isLoading.value = true
            try {
                val request = com.example.data.remote.RedeemCodeRequest(
                    input_code = code.trim(),
                    user_phone = company.repPhone
                )
                val response = com.example.data.remote.SupabaseClient.api.redeemCode(
                    apiKey = com.example.data.remote.SupabaseClient.supabaseAnonKey,
                    auth = "Bearer ${com.example.data.remote.SupabaseClient.supabaseAnonKey}",
                    request = request
                )
                if (response.isSuccessful) {
                    val result = response.body()?.toString()?.replace("\"", "") ?: "INVALID"
                    when (result) {
                        "INVALID" -> _userMessage.value = "الكود غير صحيح أو غير موجود."
                        "USED" -> _userMessage.value = "عذراً، هذا الكود تم استخدامه مسبقاً!"
                        else -> {
                            updateSubscription(result)
                            _userMessage.value = "تم تفعيل الاشتراك بنجاح! شكراً لك."
                        }
                    }
                } else {
                    _userMessage.value = "حدث خطأ في الاتصال بالسيرفر. تأكد من الإنترنت."
                }
            } catch (e: Exception) {
                _userMessage.value = "حدث خطأ: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun autoSyncIfPro() {
        viewModelScope.launch(Dispatchers.IO) {
            val company = repository.getCompanyOnce() ?: return@launch
            val isPro = company.subscriptionPlan in listOf("monthly", "yearly", "three_years")
            if (!isPro) return@launch
            try {
                val payload = repository.getAllDataForBackup()
                val request = com.example.data.remote.BackupRequest(
                    rep_phone = company.repPhone,
                    backup_data = payload,
                    last_synced = System.currentTimeMillis()
                )
                com.example.data.remote.SupabaseClient.api.upsertBackup(
                    apiKey = com.example.data.remote.SupabaseClient.supabaseAnonKey,
                    auth = "Bearer ${com.example.data.remote.SupabaseClient.supabaseAnonKey}",
                    request = request
                )
            } catch (e: Exception) { 
                /* Silently fail background sync */ 
            }
        }
    }

    fun syncNow() {
        viewModelScope.launch(Dispatchers.IO) {
            val company = repository.getCompanyOnce() ?: return@launch
            val isPro = company.subscriptionPlan in listOf("monthly", "yearly", "three_years")
            if (!isPro) {
                _userMessage.value = "هذه الميزة متاحة للمشتركين بـ Pro فقط."
                return@launch
            }
            _isLoading.value = true
            try {
                val payload = repository.getAllDataForBackup()
                val request = com.example.data.remote.BackupRequest(
                    rep_phone = company.repPhone,
                    backup_data = payload,
                    last_synced = System.currentTimeMillis()
                )
                val response = com.example.data.remote.SupabaseClient.api.upsertBackup(
                    apiKey = com.example.data.remote.SupabaseClient.supabaseAnonKey,
                    auth = "Bearer ${com.example.data.remote.SupabaseClient.supabaseAnonKey}",
                    request = request
                )
                if (response.isSuccessful) {
                    _userMessage.value = "تمت المزامنة السحابية بنجاح ☁️"
                } else {
                    _userMessage.value = "فشل في المزامنة السحابية."
                }
            } catch (e: Exception) {
                _userMessage.value = "حدث خطأ: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun autoRestoreOnLogin(company: com.example.data.local.CompanyEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = com.example.data.remote.SupabaseClient.api.getBackup(
                    apiKey = com.example.data.remote.SupabaseClient.supabaseAnonKey,
                    auth = "Bearer ${com.example.data.remote.SupabaseClient.supabaseAnonKey}",
                    phone = company.repPhone
                )
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val backupPayload = response.body()!!.first().backup_data
                    repository.restoreFromBackup(backupPayload)
                }
            } catch (e: Exception) {
                // Silently fail if no backup or network error
            }
        }
    }

    fun restoreBackup() {
        viewModelScope.launch(Dispatchers.IO) {
            val company = repository.getCompanyOnce() ?: return@launch
            _isLoading.value = true
            try {
                val response = com.example.data.remote.SupabaseClient.api.getBackup(
                    apiKey = com.example.data.remote.SupabaseClient.supabaseAnonKey,
                    auth = "Bearer ${com.example.data.remote.SupabaseClient.supabaseAnonKey}",
                    phone = company.repPhone
                )
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val backupPayload = response.body()!!.first().backup_data
                    repository.restoreFromBackup(backupPayload)
                    _userMessage.value = "تم استرجاع البيانات بنجاح 🔄"
                } else {
                    _userMessage.value = "لا توجد نسخة احتياطية محفوظة."
                }
            } catch (e: Exception) {
                _userMessage.value = "حدث خطأ أثناء الاسترجاع: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSubscription(plan: String) {
        viewModelScope.launch {
            val current = repository.getCompanyOnce() ?: return@launch
            val days = when (plan) {
                "two_weeks" -> 14
                "monthly" -> 30
                "yearly" -> 365
                "three_years" -> 1095
                else -> 7
            }
            val expiresAt = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L)
            repository.saveCompany(
                current.copy(
                    subscriptionPlan = plan,
                    subscriptionExpiresAt = expiresAt
                )
            )
            _userMessage.value = "تم تفعيل الاشتراك بنجاح!"
        }
    }
}
