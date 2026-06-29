package net.kdt.pojavlaunch.modloaders;

import net.kdt.pojavlaunch.utils.DownloadUtils;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OFDownloadPageScraper implements TagNodeVisitor {
    private static final Pattern[] DOWNLOAD_PATTERNS = new Pattern[] {
            Pattern.compile("href=[\"']([^\"']*downloadx[^\"']*)[\"']", Pattern.CASE_INSENSITIVE),
            Pattern.compile("['\"]([^'\"]*downloadx\\.php[^'\"]*)['\"]", Pattern.CASE_INSENSITIVE),
            Pattern.compile("['\"]([^'\"]*downloadx\\?[^'\"]*)['\"]", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?:https?:)?//(?:www\\.)?optifine\\.net/[^\"'<>\\s]*downloadx[^\"'<>\\s]*", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?:https?:)?//(?:www\\.)?optifined\\.net/[^\"'<>\\s]*downloadx[^\"'<>\\s]*", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(/[^\"'<>\\s]*downloadx[^\"'<>\\s]*)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(downloadx[^\"'<>\\s]*)", Pattern.CASE_INSENSITIVE)
    };

    public static String run(String urlInput) throws IOException {
        String cleanInput = normalizeEscapes(urlInput);
        if (isDirectDownloadUrl(cleanInput)) {
            return normalizeUrl(cleanInput, baseUrl(cleanInput));
        }

        String result = new OFDownloadPageScraper().runInner(cleanInput);
        if (result == null && cleanInput.contains("adloadx")) {
            String guessed = normalizeUrl(cleanInput.replace("adloadx", "downloadx"), baseUrl(cleanInput));
            if (isDirectDownloadUrl(guessed)) return guessed;
        }

        if (result == null) {
            throw new IOException("Could not find the OptiFine direct download link. Try the Mirror option or retry later.");
        }
        return result;
    }

    private String mDownloadFullUrl;
    private String mBaseUrl;

    private String runInner(String url) throws IOException {
        IOException lastError = null;

        String firstUrl = url;
        String secondUrl = url.contains("optifine.net")
                ? url.replace("optifine.net", "optifined.net")
                : url.replace("optifined.net", "optifine.net");

        for (String candidate : new String[] { firstUrl, secondUrl }) {
            if (candidate == null || candidate.isBlank()) continue;

            try {
                String scraped = performScrape(candidate);
                if (scraped != null) return scraped;
            } catch (IOException e) {
                lastError = e;
            }
        }

        if (lastError != null) throw lastError;
        return null;
    }

    private String performScrape(String url) throws IOException {
        this.mBaseUrl = baseUrl(url);
        this.mDownloadFullUrl = null;

        String htmlContent = DownloadUtils.downloadString(url);

        String regexResult = findDownloadUrlByRegex(htmlContent, mBaseUrl);
        if (regexResult != null) {
            return regexResult;
        }

        HtmlCleaner htmlCleaner = new HtmlCleaner();
        htmlCleaner.clean(htmlContent).traverse(this);

        if (mDownloadFullUrl != null) {
            return normalizeUrl(mDownloadFullUrl, mBaseUrl);
        }

        return null;
    }

    private static String findDownloadUrlByRegex(String htmlContent, String baseUrl) {
        if (htmlContent == null || htmlContent.isBlank()) return null;

        String cleanHtml = normalizeEscapes(htmlContent);

        for (Pattern pattern : DOWNLOAD_PATTERNS) {
            Matcher matcher = pattern.matcher(cleanHtml);
            while (matcher.find()) {
                String found = matcher.group(1) != null ? matcher.group(1) : matcher.group();
                found = cleanCandidate(found);
                if (isDirectDownloadUrl(found)) {
                    return normalizeUrl(found, baseUrl);
                }
            }
        }

        return null;
    }

    private static String cleanCandidate(String input) {
        if (input == null) return null;

        String cleaned = normalizeEscapes(input).trim();
        cleaned = cleaned.replace("&amp;", "&");
        cleaned = cleaned.replace("\\/", "/");

        int end = cleaned.length();
        for (String stop : new String[] { "\"", "'", "<", ">", " ", "\\n", "\\r", "\\t", ")" }) {
            int idx = cleaned.indexOf(stop);
            if (idx >= 0 && idx < end) end = idx;
        }

        return cleaned.substring(0, end);
    }

    private static String normalizeEscapes(String input) {
        if (input == null) return null;
        return input
                .replace("&amp;", "&")
                .replace("\\u0026", "&")
                .replace("\\/", "/");
    }

    private static boolean isDirectDownloadUrl(String url) {
        if (url == null) return false;
        String lower = url.toLowerCase();
        return lower.contains("downloadx") && !lower.contains("adloadx");
    }

    private static String baseUrl(String url) {
        if (url == null || !url.contains("://")) return "https://optifine.net";
        int slashIndex = url.indexOf("/", url.indexOf("://") + 3);
        if (slashIndex < 0) return url;
        return url.substring(0, slashIndex);
    }

    private static String normalizeUrl(String href, String baseUrl) {
        if (href == null) return null;

        href = cleanCandidate(href);
        if (href == null || href.isBlank()) return null;

        if (href.startsWith("//")) {
            href = "https:" + href;
        } else if (!href.startsWith("https://") && !href.startsWith("http://")) {
            if (href.startsWith("/")) {
                href = baseUrl + href;
            } else {
                href = baseUrl + "/" + href;
            }
        }

        return href.replace("http://", "https://");
    }

    @Override
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (isDownloadUrl(parentNode, htmlNode)) {
            TagNode tagNode = (TagNode) htmlNode;
            String href = tagNode.getAttributeByName("href");

            if (href == null) {
                String onclick = tagNode.getAttributeByName("onclick");
                href = findDownloadUrlByRegex(onclick, mBaseUrl);
            }

            if (href != null) {
                this.mDownloadFullUrl = normalizeUrl(href, mBaseUrl);
                return false;
            }
        }
        return true;
    }

    public boolean isDownloadUrl(TagNode parentNode, HtmlNode htmlNode) {
        if (!(htmlNode instanceof TagNode)) return false;
        TagNode tagNode = (TagNode) htmlNode;
        if (!tagNode.getName().equals("a")) return false;

        String href = tagNode.getAttributeByName("href");
        if (href != null && href.toLowerCase().contains("downloadx")) return true;

        String onclick = tagNode.getAttributeByName("onclick");
        if (onclick != null && onclick.toLowerCase().contains("downloadx")) return true;
        if ("onDownload()".equals(onclick)) return true;

        if (parentNode != null) {
            String parentId = parentNode.getAttributeByName("id");
            if ("Download".equalsIgnoreCase(parentId) && href != null) return true;
        }

        return false;
    }
}
