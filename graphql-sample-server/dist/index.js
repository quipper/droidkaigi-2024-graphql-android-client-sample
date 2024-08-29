import { ApolloServer } from '@apollo/server';
import { startStandaloneServer } from '@apollo/server/standalone';
const db = {
    blogs: {
        1: {
            title: "FigmaのCode ConnectとCode Generation APIを導入しました",
            author: "maxfie1d",
            body: `スタディサプリ小学・中学講座を開発している Android エンジニアの @maxfie1d です。 スタディサプリ小学・中学講座ではデザイナーがFigmaで作成したデザインデータをもとに...`,
            tags: [],
            publishedAt: "2024-07-16",
            likeUsers: ["hoge"],
        },
        2: {
            title: "GraphQL Trusted Documents の実装パターンを Signed Query に移行しました (Android編)",
            author: "morux2",
            body: `こんにちは、Androidエンジニアの@morux2です。スタディサプリ小学・中学講座ではデータ通信にGraphQLを採用しており、開発者が信頼したクエリのみを処理する仕組みとして...`,
            tags: ["Engineering", "Android", "Native"],
            publishedAt: "2024-05-27",
            likeUsers: ["hoge"],
        },
        3: {
            title: 'Amazon アプリストアでスタディサプリ小学・中学講座を公開するまで',
            author: "maxfie1d",
            body: `スタディサプリ小学・中学講座を開発している Android エンジニアの @maxfie1d と @omtians9425 です。2024年2月26日にスタディサプリ小学・中学講座をAmazonアプリストアにて...`,
            tags: ["Android"],
            publishedAt: "2024-04-03",
            likeUsers: ["hoge", "fuga"],
        },
        4: {
            title: "スタディサプリ小学・中学講座にRoborazziを導入して半年が経過しました",
            author: "morux2",
            body: `こんにちは、Androidエンジニアの@morux2です。スタディサプリ小学・中学講座では、Visual Regression Test (以下 VRT)を実施しています。VRTは画像比較によるUIの回帰テスト...`,
            tags: ["Engineering", "Android", "Native"],
            publishedAt: "2024-03-26",
            likeUsers: ["hoge", "fuga"],
        },
        5: {
            title: "detekt × Dangerで、プルリクコメントにルール名を表示する",
            author: "morayl",
            body: `こんにちは。スタディサプリ Androidエンジニアの@morayl です。本記事では、Kotlinの静的解析ツールであるdetektの解析結果をDangerでプルリクにコメントする際に...`,
            tags: ["Android", "Native", "Kotlin", "Ruby"],
            publishedAt: "2024-02-19",
            likeUsers: ["hoge", "fuga", "piyo"],
        },
        6: {
            title: "Android チームが使っている GitHub Actions のユニークな自動化レシピ集🍞👨‍🍳",
            author: "maxfie1d",
            body: `こスタサプ小中高を開発している Android エンジニアの@maxfie1d、@morayl とスタサプ ENGLISHを開発している Android エンジニアの田村です。GitHub Actions(以下 GHA) は...`,
            tags: ["Android", "CI/CD"],
            publishedAt: "2023-11-13",
            likeUsers: ["hoge"],
        },
        7: {
            title: "デザインをそのままの形でユーザーにお届けするためのデザインQA",
            author: "maxfie1d",
            body: `スタサプ小中で Android エンジニアをしている石田とデザイナーの竹本です。2023年9月に リニューアルをしたスタディサプリ 小学講座をリリースしました。小学開発ではエンジニアが実装した...`,
            tags: ["Design"],
            publishedAt: "2023-10-30",
            likeUsers: ["hoge", "fuga"],
        },
        8: {
            title: "スタディサプリ小学・中学講座でRoborazziを導入しました",
            author: "morux2",
            body: `こんにちは、Androidエンジニアの@morux2です。本記事ではスクリーンショットの撮影にRoborazziを導入した経緯をご紹介できればと思います。`,
            tags: ["Engineering", "Android", "Native"],
            publishedAt: "2023-10-05",
            likeUsers: ["hoge", "fuga", "piyo"],
        },
    },
    users: {
        maxfie1d: {
            name: "maxfie1d",
            image: "imageUrl",
        },
        morux2: {
            name: "morux2",
            image: "imageUrl",
        },
        morayl: {
            name: "morayl",
            image: "imageUrl",
        },
        hoge: {
            name: "hoge",
            image: "imageUrl",
        },
        fuga: {
            name: "fuga",
            image: "imageUrl",
        },
        piyo: {
            name: "piyo",
            image: "imageUrl",
        },
    },
};
const typeDefs = `#graphql
    type PostTag {
      name: String!
      posts: [Post!]!
      count: Int!
    }

    type Post  {
      id: ID!
      title: String!
      body: String!
      tags: [PostTag!]!
      publishedAt: String!
      ranking: Int!
      author: Author!
      likeUsers: [User!]!
      likeCount: Int!
    }

    interface User {
        name: String!
    }

    type Author implements User {
      id: ID!
      name: String!
      image: String!
      posts: [Post!]!
    }

    type Viewer implements User {
        name: String!
    }

    type Query {
      post(id: ID!): Post
      author(id: ID!): Author
      posts(tags: [String!]): [Post!]!
      authors: [Author!]!
    }
  `;
const resolvers = {
    Query: {
        post: (_, { id }) => {
            if (!db.blogs[id])
                return null;
            return { id, ...db.blogs[id] };
        },
        author: (_, { id }) => {
            if (!db.users[id])
                return null;
            return { id, ...db.users[id] };
        },
        posts: (_, { tags }) => Object.entries(db.blogs)
            .map(([id, post]) => ({ id, ...post }))
            .filter((post) => typeof tags === "undefined" ||
            tags.some((tag) => post.tags.includes(tag))),
        authors: () => Object.entries(db.users).map(([id, user]) => ({ id, ...user })),
    },
    Post: {
        tags: ({ tags }) => tags.map((name) => ({ name })),
        author: ({ author }) => ({ id: author, ...db.users[author] }),
        ranking: ({ id }) => {
            const ranking = Object.entries(db.blogs)
                .sort((a, b) => {
                if (a[1].likeUsers.length != b[1].likeUsers.length)
                    return b[1].likeUsers.length - a[1].likeUsers.length;
                return b[1].publishedAt.localeCompare(a[1].publishedAt);
            })
                .findIndex(([postId]) => postId === id) + 1;
            return ranking;
        },
        likeUsers: ({ likeUsers }) => likeUsers.map((id) => {
            if (!db.users[id])
                return { __typename: "Viewer", name: id };
            return { __typename: "Author", id, ...db.users[id] };
        }),
        likeCount: ({ likeUsers }) => likeUsers.length,
    },
    PostTag: {
        posts: ({ name }) => Object.entries(db.blogs)
            .filter(([_, post]) => post.tags.includes(name))
            .map(([id, post]) => ({ id, ...post })),
        count: ({ name }) => Object.entries(db.blogs).filter(([_, post]) => post.tags.includes(name))
            .length,
    },
    Author: {
        posts: ({ id }) => Object.entries(db.blogs)
            .filter(([_, post]) => post.author === id)
            .map(([id, post]) => ({ id, ...post })),
    },
};
const server = new ApolloServer({
    typeDefs,
    resolvers,
});
const { url } = await startStandaloneServer(server, {
    listen: { port: 4000 },
});
console.log(`🚀  Server ready at: ${url}`);
