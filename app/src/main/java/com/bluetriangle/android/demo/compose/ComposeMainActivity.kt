package com.bluetriangle.android.demo.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bluetriangle.analytics.compose.BttTimerEffect
import com.bluetriangle.android.demo.compose.ui.theme.BttandroidsdkTheme
import com.bluetriangle.android.demo.groupingpoc.QuoteRequestHelper
import com.bluetriangle.android.demo.groupingpoc.QuoteRequestHelper.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ComposeMainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BttandroidsdkTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(modifier: Modifier) {
    val tabs = listOf("Tab 1", "Tab 2", "Tab 3")
    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        // Pager
        HorizontalPager(
            pageCount = tabs.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            TabContent(tabName = tabs[page])
        }
    }
}

@Composable
fun TabContent(modifier: Modifier = Modifier, tabName: String) {
    BttTimerEffect(tabName)
    var quote by rememberSaveable { mutableStateOf<Quote?>(null) }
    val coroutineScope = rememberCoroutineScope()
    Column(modifier.padding(24.dp)) {
        Button(onClick = {
            coroutineScope.launch(Dispatchers.IO) {
                quote = QuoteRequestHelper.instance.getQuote()
            }
        }) {
            Text("Get Quote")
        }

        quote?.let {
            Spacer(Modifier.height(20.dp))
            QuoteCard(it)
        }
    }
}

@Composable
fun QuoteCard(quote: Quote) {
    Card {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(quote.quote)
            Spacer(Modifier.height(10.dp))
            Text(modifier = Modifier.align(Alignment.End), text = quote.author)
        }
    }
}