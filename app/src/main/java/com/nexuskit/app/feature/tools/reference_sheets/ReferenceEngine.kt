package com.nexuskit.app.feature.tools.reference_sheets

enum class ReferenceTab {
    KEYBOARD_SHORTCUTS, LINUX_COMMANDS, GIT_COMMANDS, HTML_TAGS, CSS_PROPERTIES, REGEX_GUIDE, ASCII_TABLE
}

data class ReferenceItem(
    val title: String,
    val subtitle: String,
    val category: String,
    val detail: String = ""
)

object ReferenceEngine {

    val shortcuts = listOf(
        ReferenceItem("Ctrl + C / Cmd + C", "Copy selected item to clipboard", "General"),
        ReferenceItem("Ctrl + V / Cmd + V", "Paste item from clipboard", "General"),
        ReferenceItem("Ctrl + Z / Cmd + Z", "Undo last action", "General"),
        ReferenceItem("Ctrl + Shift + Z / Cmd + Shift + Z", "Redo last undone action", "General"),
        ReferenceItem("Ctrl + P / Cmd + P", "Quick Open files by name", "VS Code"),
        ReferenceItem("Ctrl + Shift + P / Cmd + Shift + P", "Open Command Palette", "VS Code"),
        ReferenceItem("Ctrl + / / Cmd + /", "Toggle line comment", "VS Code"),
        ReferenceItem("Alt + Click / Option + Click", "Insert multiple cursors", "VS Code"),
        ReferenceItem("Ctrl + T / Cmd + T", "Open new browser tab", "Chrome"),
        ReferenceItem("Ctrl + Shift + T / Cmd + Shift + T", "Reopen last closed tab", "Chrome"),
        ReferenceItem("Ctrl + Shift + N / Cmd + Shift + N", "Open new Incognito window", "Chrome"),
        ReferenceItem("Ctrl + L / Cmd + L", "Highlight address bar", "Chrome"),
        ReferenceItem("Ctrl + Alt + K / Cmd + Option + K", "Create Component", "Figma"),
        ReferenceItem("Shift + A", "Add Auto Layout", "Figma"),
        ReferenceItem("R", "Rectangle tool", "Figma"),
        ReferenceItem("T", "Text tool", "Figma")
    )

    val linuxCommands = listOf(
        ReferenceItem("ls -la", "List all files including hidden with permissions and size", "File Management"),
        ReferenceItem("cd <path>", "Change current working directory", "Navigation"),
        ReferenceItem("pwd", "Print full path of current working directory", "Navigation"),
        ReferenceItem("mkdir -p <dir>", "Create directory including parent paths if missing", "File Management"),
        ReferenceItem("rm -rf <dir>", "Forcefully and recursively remove directory and files", "File Management"),
        ReferenceItem("cp -r <src> <dest>", "Copy directory recursively", "File Management"),
        ReferenceItem("mv <src> <dest>", "Move or rename file or directory", "File Management"),
        ReferenceItem("chmod +x <file>", "Grant executable permissions to file", "Permissions"),
        ReferenceItem("chown user:group <path>", "Change owner and group of file or directory", "Permissions"),
        ReferenceItem("grep -rnw '<pattern>' .", "Recursively search exact word pattern in current dir", "Search"),
        ReferenceItem("find . -name '*.kt'", "Find all Kotlin files in current directory subtree", "Search"),
        ReferenceItem("ps aux | grep <proc>", "List all active processes matching search term", "Process Management"),
        ReferenceItem("kill -9 <pid>", "Force kill process by PID", "Process Management"),
        ReferenceItem("top / htop", "Interactive real-time task manager and CPU/RAM monitor", "Process Management"),
        ReferenceItem("df -h", "Show disk space usage in human readable format", "System Info"),
        ReferenceItem("free -m", "Show total, used, and free memory in megabytes", "System Info"),
        ReferenceItem("curl -I <url>", "Fetch only HTTP response headers", "Networking"),
        ReferenceItem("netstat -tuln / ss -tuln", "List all active listening TCP and UDP ports", "Networking")
    )

    val gitCommands = listOf(
        ReferenceItem("git init", "Initialize new Git repository in current directory", "Setup"),
        ReferenceItem("git clone <url>", "Clone remote repository into local machine", "Setup"),
        ReferenceItem("git status", "Show working tree status (untracked, modified, staged)", "Basic"),
        ReferenceItem("git add .", "Stage all changes for the next commit", "Basic"),
        ReferenceItem("git commit -m '<msg>'", "Record staged changes to repository history", "Basic"),
        ReferenceItem("git checkout -b <branch>", "Create and switch to a new branch", "Branching"),
        ReferenceItem("git branch -a", "List all local and remote branches", "Branching"),
        ReferenceItem("git merge <branch>", "Merge specified branch into current active branch", "Branching"),
        ReferenceItem("git pull origin <branch>", "Fetch from and integrate with remote branch", "Remote"),
        ReferenceItem("git push -u origin <branch>", "Push local commits to remote repository", "Remote"),
        ReferenceItem("git stash", "Temporarily shelve uncommitted working directory changes", "Stash"),
        ReferenceItem("git stash pop", "Restore most recently stashed changes", "Stash"),
        ReferenceItem("git log --oneline -n 10", "Show condensed commit history of last 10 commits", "History"),
        ReferenceItem("git diff", "Show unstaged changes between working tree and index", "History"),
        ReferenceItem("git reset --soft HEAD~1", "Undo last commit while keeping changes staged", "Undo")
    )

    val htmlTags = listOf(
        ReferenceItem("<div>", "Generic container block element", "Layout"),
        ReferenceItem("<span>", "Generic inline container element", "Layout"),
        ReferenceItem("<header>, <main>, <footer>", "Semantic landmarks for document layout", "Semantic"),
        ReferenceItem("<section>, <article>, <nav>", "Semantic structure for sections, articles, nav links", "Semantic"),
        ReferenceItem("<h1> to <h6>", "Heading levels 1 through 6", "Typography"),
        ReferenceItem("<p>, <strong>, <em>", "Paragraph, bold (importance), and emphasis (italics)", "Typography"),
        ReferenceItem("<a>", "Anchor hyperlink element (href attribute)", "Navigation"),
        ReferenceItem("<button>", "Clickable interactive button element", "Forms"),
        ReferenceItem("<input type='...'>", "Form input field (text, number, email, password, file)", "Forms"),
        ReferenceItem("<form>", "Interactive form container for user input submission", "Forms"),
        ReferenceItem("<img>", "Embed image element (src and alt attributes)", "Media"),
        ReferenceItem("<video>, <audio>", "Native media playback controls for video and audio", "Media"),
        ReferenceItem("<table>, <tr>, <th>, <td>", "Tabular data structure elements", "Tables")
    )

    val asciiTable = (0..127).map { code ->
        val charStr = when (code) {
            0 -> "NUL"
            9 -> "\\t"
            10 -> "\\n"
            13 -> "\\r"
            32 -> "SPACE"
            in 33..126 -> code.toChar().toString()
            127 -> "DEL"
            else -> "CTRL"
        }
        val hex = Integer.toHexString(code).uppercase().padStart(2, '0')
        val bin = Integer.toBinaryString(code).padStart(8, '0')
        val oct = Integer.toOctalString(code).padStart(3, '0')
        ReferenceItem("Dec: $code | Char: $charStr", "Hex: 0x$hex | Bin: $bin | Oct: $oct", "ASCII", "Code Point $code")
    }
}
