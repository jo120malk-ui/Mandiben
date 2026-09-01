import re

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

pattern = re.compile(r'ProductsScreen\(.*?\n\s*\)', re.DOTALL)
replacement = """ProductsScreen(
                            products = products,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onOpenAddProductDialog = { p -> viewModel.openAddProductDialog(p) },
                            onDeleteProduct = { id -> viewModel.deleteProduct(id.toString().toInt()) }
                        )"""

# but wait! product.id is Int in the db maybe? The error says `Argument type mismatch: actual type is 'Int', but 'String' was expected.` on line 96 in ProductsScreen.kt!
