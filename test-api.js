const http = require('http');

const request = (path, method, data, token) => new Promise((resolve, reject) => {
  const options = {
    hostname: 'localhost',
    port: 8082,
    path: path,
    method: method,
    headers: {
      'Content-Type': 'application/json'
    }
  };
  if (token) options.headers['Authorization'] = `Bearer ${token}`;

  const req = http.request(options, res => {
    let body = '';
    res.on('data', chunk => body += chunk);
    res.on('end', () => {
      try {
        resolve({ status: res.statusCode, body: JSON.parse(body || '{}') });
      } catch (e) {
        resolve({ status: res.statusCode, body: body });
      }
    });
  });
  req.on('error', reject);
  if (data) req.write(JSON.stringify(data));
  req.end();
});

(async () => {
    try {
        const rand = Math.floor(Math.random() * 100000);
        const email = `test.node.${rand}@example.com`;
        const username = `testnode${rand}`;
        const pass = "Password123!";
        
        console.log("Registering...", username);
        const regRes = await request('/api/auth/register', 'POST', {
            username: username,
            email: email,
            password: pass,
            confirmPassword: pass,
            fullName: "Test User",
            phoneNumber: "1234567890"
        });
        
        console.log("Logging in...");
        const loginRes = await request('/api/auth/login', 'POST', {
            usernameOrEmail: email,
            password: pass
        });
        const token = loginRes.body?.data?.accessToken;
        if (!token) {
            console.error("Login failed!", loginRes);
            return;
        }

        const now = new Date();
        const dStr = now.getFullYear() + "-" + String(now.getMonth()+1).padStart(2, '0') + "-" + String(now.getDate()).padStart(2, '0');
        now.setMinutes(now.getMinutes() + 2);
        const timeStr = String(now.getHours()).padStart(2, '0') + ":" + String(now.getMinutes()).padStart(2, '0');
        
        console.log("Creating reminder for date:", dStr, "time:", timeStr);

        const remRes = await request('/api/reminders', 'POST', {
            title: "Diagnostic Reminder",
            frequency: "DAILY",
            startDate: dStr,
            doseAmount: 1,
            doseUnit: "mg",
            snoozeMinutes: 10,
            notificationType: "PUSH",
            reminderTimes: [timeStr]
        }, token);

        console.log("Fetching today's reminders...");
        const todayRes = await request('/api/reminders/today', 'GET', null, token);
        console.log("Today Data:");
        console.log(JSON.stringify(todayRes.body.data[0], null, 2));

    } catch (e) {
        console.error("Script Error:", e);
    }
})();
