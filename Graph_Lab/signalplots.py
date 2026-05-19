import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np

# Set a professional statistical style
sns.set_theme(style="darkgrid", context="talk")

# =================================================================
# Plot 1: MST Statistical Trend Plot (Line + Error Bars)
# =================================================================
mst_df = pd.read_csv('MST_Results.csv')
plt.figure(figsize=(10, 6))

algorithms = mst_df['Algorithm'].unique()
colors = ['#1f77b4', '#ff7f0e']

for idx, algo in enumerate(algorithms):
    subset = mst_df[mst_df['Algorithm'] == algo]
    plt.errorbar(subset['Graph Topology'], subset['Mean (ms)'], 
                 yerr=subset['Standard Deviation (ms)'], 
                 label=algo, fmt='-o', capsize=5, capthick=2, 
                 color=colors[idx], markersize=8, linewidth=2)

plt.title("MST Construction: Scaling and Variance", fontsize=16, fontweight='bold')
plt.ylabel('Execution Time (ms)', fontsize=14)
plt.xlabel('Graph Density', fontsize=14)
plt.legend()
plt.tight_layout()

plt.savefig('MST_Statistical_Trend.png', dpi=300)
plt.show()

# =================================================================
# Plot 2: SSSP Dijkstra Trend Plot (Line + Error Bars)
# =================================================================
sssp_df = pd.read_csv('SSSP_Results.csv')
# Filter out DAG to only show the Sparse -> Complete scaling
sssp_scaling = sssp_df[sssp_df['Graph Topology'].str.contains('Graph')]

plt.figure(figsize=(10, 6))
plt.errorbar(sssp_scaling['Graph Topology'], sssp_scaling['Mean (ms)'], 
             yerr=sssp_scaling['Standard Deviation (ms)'], 
             label="Dijkstra's Algorithm", fmt='-s', capsize=5, capthick=2, 
             color='#2ca02c', markersize=8, linewidth=2)

plt.title("Dijkstra's SSSP: Performance Variance Across Densities", fontsize=16, fontweight='bold')
plt.ylabel('Execution Time (ms)', fontsize=14)
plt.xlabel('Graph Density', fontsize=14)
plt.legend()
plt.tight_layout()

plt.savefig('SSSP_Statistical_Trend.png', dpi=300)
plt.show()

# =================================================================
# Plot 3: DAG Algorithm Statistical Comparison (V=5000 vs V=7000)
# =================================================================
dag5k = pd.read_csv('DAG_Results.csv')
dag7k = pd.read_csv('DAG_Results7000.csv')

# Add vertices column
dag5k['Vertices'] = 5000
dag7k['Vertices'] = 7000

# Merge datasets
dag_combined = pd.concat([dag5k, dag7k], ignore_index=True)

plt.figure(figsize=(10, 6))
algo_colors = {'Dijkstra': '#1f77b4', 'Linear (DFS)': '#d62728', 'Linear (Kahn)': '#9467bd'}

for algo in dag_combined['Algorithm'].unique():
    subset = dag_combined[dag_combined['Algorithm'] == algo]
    plt.errorbar(subset['Vertices'], subset['Mean (ms)'], 
                 yerr=subset['Standard Deviation (ms)'], 
                 label=algo, fmt='-^', capsize=5, capthick=2, 
                 color=algo_colors.get(algo, '#333333'), markersize=9, linewidth=2)

plt.title("DAG Algorithms: Crossover Point & Stability", fontsize=16, fontweight='bold')
plt.ylabel('Execution Time (ms)', fontsize=14)
plt.xlabel('Number of Vertices (V)', fontsize=14)
plt.xticks([5000, 7000])
plt.legend(title="Algorithm")
plt.tight_layout()

plt.savefig('DAG_Statistical_Crossover.png', dpi=300)
plt.show()