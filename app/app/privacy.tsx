import { ScrollView, View, Text, StyleSheet, Platform } from "react-native";
import { router } from "expo-router";
import { TouchableOpacity } from "react-native";

export default function PrivacyPolicyScreen() {
  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      <TouchableOpacity style={styles.back} onPress={() => router.back()}>
        <Text style={styles.backText}>← Back</Text>
      </TouchableOpacity>

      <Text style={styles.h1}>Privacy Policy</Text>
      <Text style={styles.meta}>Uchia · Last updated: April 12, 2025</Text>

      <Text style={styles.p}>
        This Privacy Policy describes how Uchia ("we", "us", or "our") handles your information
        when you use the Uchia app and website (collectively, the "Service").
      </Text>

      <Text style={styles.h2}>1. Information We Collect</Text>
      <Text style={styles.p}>
        <Text style={styles.bold}>Account information:</Text> When you sign up, we collect your
        email address via Supabase Auth. We do not store passwords — authentication is handled
        securely by Supabase.
      </Text>
      <Text style={styles.p}>
        <Text style={styles.bold}>Usage data:</Text> We track how many summaries you have
        generated in order to enforce free-tier limits. This counter is stored in our database
        and resets every 24 hours.
      </Text>
      <Text style={styles.p}>
        <Text style={styles.bold}>URLs you submit:</Text> When you submit a YouTube Shorts URL
        for summarization, it is sent to our backend for processing. We do not permanently store
        submitted URLs beyond the time needed to generate your summary.
      </Text>

      <Text style={styles.h2}>2. Information We Do Not Collect</Text>
      <Text style={styles.p}>
        We do not collect your location, contacts, camera access, microphone (except optionally
        for Live Translation features on Android), or any other personal data beyond what is
        described above.
      </Text>

      <Text style={styles.h2}>3. How We Use Your Information</Text>
      <Text style={styles.p}>
        We use your information solely to provide and improve the Service — specifically to
        authenticate you, process your summarization requests, and enforce usage limits.
        We do not sell, rent, or share your data with third parties for advertising purposes.
      </Text>

      <Text style={styles.h2}>4. Third-Party Services</Text>
      <Text style={styles.p}>
        The Service relies on the following third-party providers, each with their own privacy
        policies:
      </Text>
      <Text style={styles.li}>• <Text style={styles.bold}>Supabase</Text> — authentication and database (supabase.com/privacy)</Text>
      <Text style={styles.li}>• <Text style={styles.bold}>Railway</Text> — backend hosting (railway.app/legal/privacy)</Text>
      <Text style={styles.li}>• <Text style={styles.bold}>Google ML Kit</Text> — on-device OCR and translation (no data leaves your device)</Text>

      <Text style={styles.h2}>5. Data Retention</Text>
      <Text style={styles.p}>
        Your account data is retained for as long as your account is active. You may request
        deletion of your account and associated data at any time by contacting us.
      </Text>

      <Text style={styles.h2}>6. Children's Privacy</Text>
      <Text style={styles.p}>
        The Service is not directed to children under the age of 13. We do not knowingly collect
        personal information from children.
      </Text>

      <Text style={styles.h2}>7. Changes to This Policy</Text>
      <Text style={styles.p}>
        We may update this Privacy Policy from time to time. We will notify you of significant
        changes by updating the date at the top of this page.
      </Text>

      <Text style={styles.h2}>8. Contact</Text>
      <Text style={styles.p}>
        If you have any questions about this Privacy Policy, please contact us at:
        {"\n"}lisumchen@gmail.com
      </Text>

      <View style={styles.footer}>
        <Text style={styles.footerText}>© 2025 Uchia. All rights reserved.</Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#08090a" },
  content: {
    paddingHorizontal: Platform.OS === "web" ? "20%" : 24,
    paddingTop: 48,
    paddingBottom: 64,
    maxWidth: Platform.OS === "web" ? 800 : undefined,
    alignSelf: Platform.OS === "web" ? "center" : undefined,
    width: "100%",
  },
  back: { marginBottom: 32 },
  backText: { color: "#7170ff", fontSize: 15 },
  h1: {
    fontSize: 36,
    fontWeight: "700",
    color: "#f7f8f8",
    letterSpacing: -1,
    marginBottom: 8,
  },
  meta: { color: "#62666d", fontSize: 13, marginBottom: 32 },
  h2: {
    fontSize: 18,
    fontWeight: "600",
    color: "#f7f8f8",
    marginTop: 28,
    marginBottom: 10,
  },
  p: { color: "#a0a6b0", fontSize: 14, lineHeight: 22, marginBottom: 12 },
  li: { color: "#a0a6b0", fontSize: 14, lineHeight: 22, marginBottom: 6, paddingLeft: 8 },
  bold: { color: "#d0d6e0", fontWeight: "600" },
  footer: { marginTop: 48, alignItems: "center" },
  footerText: { color: "#62666d", fontSize: 12 },
});
