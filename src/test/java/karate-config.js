function fn() {

    var config = {
        baseUrl: 'http://localhost:8080'
    };
    if (karate.env == 'chrome-linux') {
        karate.configure('driver', { executable: '/usr/bin/chromium-browser', type: 'chrome', showDriverLog: true });
    } else if (karate.env == 'gecko') {
        karate.configure('driver', { type: 'geckodriver', executable: '/home/mfreire/Downloads/geckodriver' });
    } else {
        karate.configure('driver', { type: 'chrome', showDriverLog: true });
    }

    return config;
}