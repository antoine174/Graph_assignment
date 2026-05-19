import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Set professional styling
sns.set_theme(style="whitegrid", palette="muted")

# =================================================================
# Plot 1: MST Construction Benchmarks
# =================================================================
mst_df = pd.read_csv('MST_Results.csv')
plt.figure(figsize=(10, 6))
sns.barplot(data=mst_df, x='Graph Topology', y='Mean (ms)', hue='Algorithm')

plt.title("1. MST Construction: Prim's vs. Kruskal's", fontsize=14, fontweight='bold')
plt.ylabel('Mean Execution Time (ms)', fontsize=12)
plt.xlabel('Graph Density', fontsize=12)
plt.xticks(rotation=10)
plt.tight_layout()

plt.savefig('MST_Comparison.png', dpi=300)
plt.show()

# =================================================================
# Plot 2: SSSP on General Graphs (Dijkstra)
# =================================================================
sssp_df = pd.read_csv('SSSP_Results.csv')
plt.figure(figsize=(10, 6))
sns.barplot(data=sssp_df, x='Graph Topology', y='Mean (ms)', color='cornflowerblue')

plt.title("2. SSSP Calculation: Dijkstra's Algorithm across Densities", fontsize=14, fontweight='bold')
plt.ylabel('Mean Execution Time (ms)', fontsize=12)
plt.xlabel('Graph Topology', fontsize=12)
plt.xticks(rotation=15)
plt.tight_layout()

plt.savefig('SSSP_Dijkstra_Comparison.png', dpi=300)
plt.show()

# =================================================================
# Plot 3: DAG Topology Comparison (V=5000 vs V=7000)
# =================================================================
dag5k = pd.read_csv('DAG_Results.csv')
dag7k = pd.read_csv('DAG_Results7000.csv')

# Add a column to distinguish the two datasets
dag5k['Vertices'] = 'V = 5,000'
dag7k['Vertices'] = 'V = 7,000'

# Merge them together for a grouped bar chart
dag_combined = pd.concat([dag5k, dag7k], ignore_index=True)

plt.figure(figsize=(10, 6))
sns.barplot(data=dag_combined, x='Algorithm', y='Mean (ms)', hue='Vertices', palette=['#2ca02c', '#d62728'])

plt.title("3. SSSP on DAG: Dijkstra vs. Topological Sort Algorithms", fontsize=14, fontweight='bold')
plt.ylabel('Mean Execution Time (ms)', fontsize=12)
plt.xlabel('Algorithm', fontsize=12)
plt.legend(title='Graph Size')
plt.tight_layout()

plt.savefig('DAG_Comparison.png', dpi=300)
plt.show()