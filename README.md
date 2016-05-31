# Standard-Blog-App
This is a standard and simple Android app that you can easily make for your Blog (without the need to actually know how to program).
It's basically a single open-source RSS feed reader for your own blog.

## How to build an App for my Blog?
1. Set up **Android Studio** with the needed APIs.
2. Import this project to the IDE.
3. Go to *res/Strings.xml* and change the marked Strings as needed.
4. Images can be changed by replacing the existing ones (DO NOT CHANGE THE NAME) on the *res/drawable-?dpi* folders.
5. For further customization, you can also go to the *colors.xml* file to adequate it to your blog's colors.
6. Before release, remember to set your own **applicationID**.


## TODO
- [ ] Background syncing;
- [x] Sync when the app opens (only on the prefered connection and if the last sync was more than X hours);
- [ ] Fix settings;
- [ ] Icon color;
- [ ] Google Analytics;
- [ ] Replace OkHTTP w/ JSOUP (I don't need both for what I'm using);
- [ ] Pass Headers to request to see if it fixes the *cache* problem;
- [ ] "My Ads" ~~complete with description and content, plus~~ open link directly to Google Play;
- [ ] Set ads to a different color;
- [ ] Master detail on landscape. List of posts on the left, and post on the right.


### Next Update
- Widget (v2 only);
- Give the user an option to view the image or follow the link on image click;
- Open app from Web link (Low priority, since we're not parsing from the webpage and that would only take you to the app's main page and not the especific post, although we could do a search to find this post, but it may not be saved on the DB);
- See also (Next unseen post) - At the bottom of each Publication;
- Admob.
