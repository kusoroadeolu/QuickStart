#!/bin/bash

echo ""
echo "============================================"
echo "  QuickStart Installer (Unix/Linux/macOS)"
echo "============================================"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "[ERROR] Java is not installed or not in PATH."
    echo "Please install Java 17 or higher and try again."
    echo ""
    exit 1
fi

# Check if quickstart.jar exists in multiple locations
JAR_PATH=""
if [ -f "quickstart.jar" ]; then
    JAR_PATH="quickstart.jar"
    echo "[INFO] Found quickstart.jar in current directory"
elif [ -f "target/quickstart.jar" ]; then
    JAR_PATH="target/quickstart.jar"
    echo "[INFO] Found quickstart.jar in target directory"
else
    echo "[ERROR] quickstart.jar not found."
    echo ""
    echo "Please either:"
    echo "  1. Download quickstart.jar from GitHub releases and place it here"
    echo "  2. Build from source with 'mvn clean package'"
    echo ""
    exit 1
fi

echo "[1/4] Creating QuickStart directory..."
mkdir -p ~/.quickstart

echo "[2/4] Copying JAR file..."
cp "$JAR_PATH" ~/.quickstart/quickstart.jar

echo "[3/4] Creating command wrapper..."
# Create short alias
cat > ~/.quickstart/qs << 'EOF'
#!/bin/bash
java -jar ~/.quickstart/quickstart.jar "$@"
EOF

chmod +x ~/.quickstart/qs

echo "[4/4] Adding to PATH..."

# Detect shell config file
SHELL_CONFIG=""
if [ -n "$ZSH_VERSION" ]; then
    SHELL_CONFIG="$HOME/.zshrc"
elif [ -n "$BASH_VERSION" ]; then
    if [ -f "$HOME/.bashrc" ]; then
        SHELL_CONFIG="$HOME/.bashrc"
    elif [ -f "$HOME/.bash_profile" ]; then
        SHELL_CONFIG="$HOME/.bash_profile"
    fi
fi

# Auto-detect if not found
if [ -z "$SHELL_CONFIG" ]; then
    if [ -f "$HOME/.zshrc" ]; then
        SHELL_CONFIG="$HOME/.zshrc"
    elif [ -f "$HOME/.bashrc" ]; then
        SHELL_CONFIG="$HOME/.bashrc"
    elif [ -f "$HOME/.bash_profile" ]; then
        SHELL_CONFIG="$HOME/.bash_profile"
    else
        SHELL_CONFIG="$HOME/.bashrc"  # Create if nothing exists
    fi
fi

PATH_EXPORT='export PATH="$HOME/.quickstart:$PATH"'

# Check if already in config
if grep -q ".quickstart" "$SHELL_CONFIG" 2>/dev/null; then
    echo "   > Already in PATH ($SHELL_CONFIG), skipping"
else
    echo "" >> "$SHELL_CONFIG"
    echo "# QuickStart CLI" >> "$SHELL_CONFIG"
    echo "$PATH_EXPORT" >> "$SHELL_CONFIG"
    echo "   > Added to PATH in $SHELL_CONFIG"
fi

echo ""
echo "============================================"
echo "  Installation Complete!"
echo "============================================"
echo ""
echo "QuickStart has been installed to:"
echo "  ~/.quickstart"
echo ""
echo "IMPORTANT: Run this command to refresh your shell:"
echo "  source $SHELL_CONFIG"
echo ""
echo "Or just open a new terminal window."
echo ""
echo "Try these commands:"
echo "  qs init"
echo "  qs --help"
echo ""