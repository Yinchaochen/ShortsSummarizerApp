import { View, Text, TouchableOpacity, StyleSheet, Linking } from "react-native";
import { FontAwesome } from "@expo/vector-icons";

const SOCIAL_LINKS = [
  { icon: "github" as const,   url: "https://github.com/Yinchaochen",                              label: "GitHub" },
  { icon: "medium" as const,   url: "https://medium.com/@lisumchen",                               label: "Medium" },
  { icon: "youtube" as const,  url: "https://www.youtube.com/@LisumChen",                          label: "YouTube" },
  { icon: "twitter" as const,  url: "https://x.com/lisum292",                                      label: "X" },
  { icon: "linkedin" as const, url: "https://www.linkedin.com/in/yinchao-chen-848038308",          label: "LinkedIn" },
];

const APP_LINKS = [
  {
    icon: "apple" as const,
    store: "App Store",
    sub: "Download on the",
    url: "https://apps.apple.com",
  },
  {
    icon: "android" as const,
    store: "Google Play",
    sub: "Get it on",
    url: "https://play.google.com",
  },
  {
    icon: "download" as const,
    store: "Direct Download",
    sub: "Get the APK",
    url: "https://github.com/Yinchaochen/ShortsSummarizerApp/releases/latest",
  },
];

export default function Footer() {
  return (
    <View style={styles.footer}>
      <View style={styles.divider} />

      <View style={styles.appButtons}>
        {APP_LINKS.map((item) => (
          <TouchableOpacity
            key={item.store}
            style={styles.appButton}
            onPress={() => Linking.openURL(item.url)}
            accessibilityLabel={`${item.sub} ${item.store}`}
          >
            <FontAwesome name={item.icon} size={22} color="#f7f8f8" style={styles.appIcon} />
            <View>
              <Text style={styles.appSub}>{item.sub}</Text>
              <Text style={styles.appStore}>{item.store}</Text>
            </View>
          </TouchableOpacity>
        ))}
      </View>

      <View style={styles.icons}>
        {SOCIAL_LINKS.map((item) => (
          <TouchableOpacity
            key={item.label}
            style={styles.iconButton}
            onPress={() => Linking.openURL(item.url)}
            accessibilityLabel={item.label}
          >
            <FontAwesome name={item.icon} size={18} color="#62666d" />
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  footer: {
    marginTop: 48,
    alignItems: "center",
  },
  divider: {
    width: "100%",
    height: 1,
    backgroundColor: "rgba(255,255,255,0.06)",
    marginBottom: 24,
  },
  appButtons: {
    flexDirection: "column",
    gap: 10,
    marginBottom: 20,
    alignSelf: "stretch",
  },
  appButton: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "rgba(255,255,255,0.05)",
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.12)",
    borderRadius: 10,
    paddingHorizontal: 16,
    paddingVertical: 12,
    gap: 12,
  },
  appIcon: {
    opacity: 0.9,
  },
  appSub: {
    color: "#62666d",
    fontSize: 10,
    letterSpacing: 0.3,
  },
  appStore: {
    color: "#f7f8f8",
    fontSize: 14,
    fontWeight: "600",
    letterSpacing: -0.2,
  },
  icons: {
    flexDirection: "row",
    gap: 8,
  },
  iconButton: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: "rgba(255,255,255,0.04)",
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.07)",
    alignItems: "center",
    justifyContent: "center",
  },
});
