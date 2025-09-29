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

print(f"Testing with API key: {api_key[:20]}..." + "*" * (len(api_key) - 20))
print("=" * 60)

# List available models
list_models_url = f"https://generativelanguage.googleapis.com/v1beta/models?key={api_key}"

try:
    response = requests.get(list_models_url)
    print(f"List Models Status Code: {response.status_code}")
    
    if response.status_code == 200:
        data = response.json()
        models = data.get('models', [])
        print(f"✅ Found {len(models)} total models")
        
        # Find models that support generateContent
        working_models = []
        print("\nModels that support generateContent:")
        print("-" * 40)
        
        for model in models:
            model_name = model.get('name', '').replace('models/', '')
            supported_methods = model.get('supportedGenerationMethods', [])
            
            if 'generateContent' in supported_methods:
                working_models.append(model_name)
                print(f"✅ {model_name}")
            else:
                print(f"❌ {model_name} (methods: {supported_methods})")
        
        if working_models:
            print(f"\n🎯 Found {len(working_models)} working models")
            print("Testing the first few models...")
            print("-" * 40)
            
            # Test first few working models
            for i, model_name in enumerate(working_models[:3]):
                print(f"\n🧪 Testing {model_name}:")
                
                test_url = f"https://generativelanguage.googleapis.com/v1beta/models/{model_name}:generateContent?key={api_key}"
                test_payload = {
                    "contents": [{
                        "parts": [{
                            "text": "Hello, respond with just 'Hi'"
                        }]
                    }]
                }
                
                test_response = requests.post(
                    test_url, 
                    headers={'Content-Type': 'application/json'}, 
                    data=json.dumps(test_payload),
                    timeout=30
                )
                
                if test_response.status_code == 200:
                    try:
                        result = test_response.json()
                        text = result['candidates'][0]['content']['parts'][0]['text']
                        print(f"  ✅ SUCCESS - Response: {text.strip()}")
                        print(f"  📋 Use this in your Android app: \"{model_name}\"")
                        break
                    except:
                        print(f"  ⚠️  Got 200 but couldn't parse response")
                else:
                    error_text = test_response.text[:200] + "..." if len(test_response.text) > 200 else test_response.text
                    print(f"  ❌ Failed ({test_response.status_code}): {error_text}")
            
            print(f"\n📝 Recommended models to try in your Android app:")
            for model in working_models[:5]:
                print(f"   • \"{model}\"")
        else:
            print("❌ No models found that support generateContent")
    
    elif response.status_code == 403:
        error_data = response.json() if response.content else {}
        print("❌ API Key is invalid or doesn't have permission")
        print(f"Error details: {error_data}")
    else:
        print(f"❌ Error {response.status_code}")
        try:
            error_data = response.json()
            print(f"Error details: {error_data}")
        except:
            print(f"Error text: {response.text}")

except Exception as e:
    print(f"❌ Request failed: {str(e)}")