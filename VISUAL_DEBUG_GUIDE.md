# Visual Debug Guide - See Exactly Where It Fails

## What I Added

I've added **Toast messages** at every step, so you can see exactly where the app is failing!

## How to Test - Follow These Steps:

### Step 1: Rebuild the Project
1. In Android Studio: **Build** → **Rebuild Project**
2. Wait for it to complete

### Step 2: Run and Watch Carefully
1. Click the **Run** button (green triangle)
2. Enter your login credentials
3. Click **Login**
4. **WATCH THE SCREEN CAREFULLY** for Toast messages (small popup messages)

## What You Should See (If Everything Works):

After clicking Login, you should see these Toast messages appear one by one:

```
1. "Login successful!"
2. "Session saved! Loading home..."
3. "HomeActivity started!"
4. "Layout loaded!"
5. "User verified! Setting up UI..."
6. "Home loaded successfully!"
```

Then you should see the **Home screen** with your welcome card!

## If the App Closes - What Messages Did You See?

### Scenario A: Only saw "Login successful!"
**Problem**: Session not being saved
**What to check**: API response might not have token or user data

### Scenario B: Saw "Session saved! Loading home..." then app closed
**Problem**: HomeActivity is crashing before it can start
**What to check**: Logcat for the exact crash

### Scenario C: Saw "HomeActivity started!" then app closed
**Problem**: Layout inflation error
**What to check**: Binding or layout XML issue

### Scenario D: Saw "Layout loaded!" then app closed
**Problem**: Error in setupUserInfo or setupClickListeners
**What to check**: User data or view binding issue

### Scenario E: Saw error message like "ERROR: ..."
**Perfect!** The app is telling you exactly what's wrong!
**What to do**: Tell me what the error message says

## How to Check Logcat (Very Important!)

Even with Toast messages, we need Logcat to see the full error:

1. **Open Logcat** in Android Studio:
   - Click **View** → **Tool Windows** → **Logcat** (or press Alt+6)

2. **Set up filters**:
   - In the filter dropdown, select "Error" level
   - In the search box, type: `AndroidRuntime`

3. **Run the app and login**

4. **Look for RED text** that says:
   ```
   FATAL EXCEPTION: main
   Process: group.eleven.snippet_sharing_app, PID: xxxxx
   ```

5. **Copy the entire error** (it will be 10-30 lines) and share it with me!

## Common Errors and What They Mean:

### Error: "java.lang.NullPointerException"
**Meaning**: Something is null that shouldn't be
**Location**: The stack trace will show exactly which line

### Error: "android.content.ActivityNotFoundException"
**Meaning**: HomeActivity isn't registered properly
**Solution**: Check AndroidManifest.xml (but I already verified this is OK)

### Error: "java.lang.ClassNotFoundException"
**Meaning**: The activity class can't be found
**Solution**: Rebuild project

### Error: "android.view.InflateException"
**Meaning**: Error loading the layout XML
**Location**: Check which layout file is mentioned

## What to Share With Me:

Please tell me:

1. **Which Toast messages did you see?** (List them in order)

2. **What's in Logcat?** (Copy the RED error text that starts with "FATAL EXCEPTION")

3. **Did you see any error Toast?** (Like "ERROR: ...")

## Quick Test - Are You Connected to API?

Before testing login, let me verify your API is working:

**Check your API URL:**
- File: `app/build.gradle.kts`
- Line 19: `buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2/api/v1/\"")`

**Questions:**
- Are you running the backend server?
- Is it running on `localhost` or a different IP?
- If using a physical device, `10.0.2.2` won't work (it's for emulator only)

**For Physical Device:**
Change `10.0.2.2` to your computer's local IP (e.g., `192.168.1.100`)

---

## Ready to Test!

1. ✅ Rebuild project
2. ✅ Run the app
3. ✅ Login
4. ✅ Watch for Toast messages
5. ✅ Check Logcat for errors
6. ✅ Tell me what you see!

Let's find out exactly where it's failing! 🔍
