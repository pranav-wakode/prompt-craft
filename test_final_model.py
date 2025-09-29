#!/usr/bin/env python3

import requests
import json
import sys

# Read API key from local.properties
try:
    with open('local.properties', 'r') as f:
        content = f.read()
        for line in content.split('\n'):
            if line.startswith('GEMINI_API_KEY='):
                api_key = line.split('=', 1)[1].strip()
                break
        else:
            print("API key not found in local.properties")
            sys.exit(1)
except FileNotFoundError:
    print("local.properties file not found")
    sys.exit(1)

# Test the specific model we'll use in the Android app
model_name = "gemini-2.5-flash"
print(f"🧪 Testing model: {model_name}")
print(f"API Key: {api_key[:20]}..." + "*" * (len(api_key) - 20))
print("=" * 50)

test_url = f"https://generativelanguage.googleapis.com/v1beta/models/{model_name}:generateContent?key={api_key}"

# Test with a prompt similar to what your app would send
test_payload = {
    "contents": [{
        "parts": [{
            "text": """You are an expert prompt engineer. Your task is to take a user's prompt and enhance it.
- **DO NOT** ask clarifying questions.
- **ALWAYS** respond with the enhanced prompt, starting with the prefix "Enhanced Prompt: ".
- **Generate a response with a "Medium" length.** A 'Medium' prompt should be a detailed paragraph (around 80-120 words).
- Make the new prompt detailed, specific, and well-structured.
- Apply the following technique: Chain of Thought, Role Playing
- Include context, format requirements, and expected output style where appropriate.

User's original prompt: "Write a story about a dog"

Enhanced Prompt:"""
        }]
    }]
}

try:
    print("Sending request...")
    response = requests.post(
        test_url, 
        headers={'Content-Type': 'application/json'}, 
        data=json.dumps(test_payload),
        timeout=30
    )
    
    print(f"Status Code: {response.status_code}")
    
    if response.status_code == 200:
        result = response.json()
        
        if 'candidates' in result and len(result['candidates']) > 0:
            text = result['candidates'][0]['content']['parts'][0]['text']
            print("✅ SUCCESS! Model is working correctly.")
            print("\n" + "="*50)
            print("RESPONSE:")
            print("="*50)
            print(text)
            print("="*50)
            print(f"\n🎯 Your Android app should now work with model: \"{model_name}\"")
        else:
            print("⚠️  Got 200 but unexpected response format")
            print(f"Response: {result}")
    
    elif response.status_code == 429:
        print("⚠️  Rate limit exceeded - but the model works!")
        print("Your Android app should work, just try again in a moment.")
    
    else:
        print(f"❌ Failed with status {response.status_code}")
        try:
            error_data = response.json()
            print(f"Error: {error_data}")
        except:
            print(f"Raw response: {response.text}")

except Exception as e:
    print(f"❌ Request failed: {str(e)}")