package jp.morux2.graphqlClientSample2024.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.morux2.graphqlClientSample2024.fragment.PostCardFragment

@Composable
fun PostCard(
    postCardFragment: PostCardFragment,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = postCardFragment.author.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = postCardFragment.title,
                fontSize = 18.sp
            )
            Text(
                text = postCardFragment.body,
                fontSize = 12.sp
            )
        }
    }
}

@Preview
@Composable
private fun PostCardPreview() {
    PostCard(
        modifier = Modifier.fillMaxWidth(),
        postCardFragment = dummyPostCardFragment,
    )
}

val dummyPostCardFragment = PostCardFragment(
    author = PostCardFragment.Author(
        name = "author name"
    ),
    title = "title",
    body = "body"
)