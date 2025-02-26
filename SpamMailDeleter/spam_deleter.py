import os
import imaplib
import time
from flask import Flask
from threading import Thread

# Flask app for keep-alive
app = Flask(__name__)

@app.route('/')
def home():
    return "Email Deletion Service Running"

# Health check route for Render
@app.route('/healthz')
def health_check():
    return "OK", 200

def run():
    app.run(host='0.0.0.0', port=8080)

def keep_alive():
    t = Thread(target=run)
    t.start()

def delete_spam_emails():
    EMAIL = os.environ.get("EMAIL")
    APP_PASSWORD = os.environ.get("APP_PASSWORD")

    if not EMAIL or not APP_PASSWORD:
        raise ValueError(
            "⚠️ Email credentials not found. Please set EMAIL and APP_PASSWORD as environment variables."
        )

    # Connect to Gmail
    while True:
        try:
            print("🔄 Connecting to Gmail IMAP server...")
            mail = imaplib.IMAP4_SSL("imap.gmail.com")
            mail.login(EMAIL, APP_PASSWORD)
            print("✅ Logged in successfully!")

            # List available mailboxes
            status, mailboxes = mail.list()
            print("📁 Available Mailboxes:")
            for mbox in mailboxes:
                print(mbox.decode())

            # Select Spam folder dynamically
            spam_folders = ['[Gmail]/Spam', 'Spam', '[Google Mail]/Spam']
            selected = False

            for folder in spam_folders:
                status, messages = mail.select(f'"{folder}"', readonly=False)
                if status == "OK":
                    print(f"📂 Selected folder: {folder}")
                    selected = True
                    break

            if not selected:
                print("❌ Spam folder not found. Check folder names above.")
                mail.logout()
                time.sleep(150)
                continue

            # Search and delete emails
            status, data = mail.search(None, 'ALL')
            email_ids = data[0].split()

            if not email_ids:
                print("📭 No emails found in Spam.")
            else:
                print(f"🗑️ Found {len(email_ids)} emails. Deleting...")
                for email_id in email_ids:
                    mail.store(email_id, '+FLAGS', '\\Deleted')

                mail.expunge()
                print("✅ Emails deleted successfully.")

            mail.logout()

        except imaplib.IMAP4.error as e:
            print(f"⚠️ IMAP error: {e}")
        except Exception as e:
            print(f"⚠️ Unexpected error: {e}")

        print("⏳ Waiting 5 minutes before next check...\n")
        time.sleep(300)

# Start the services
keep_alive()
delete_spam_emails()
