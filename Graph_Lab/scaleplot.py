import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Load data
df = pd.read_csv('DAG_Scaling_Results.csv')

# Sort data to ensure bars are in a consistent order
df = df.sort_values(by=['Runs', 'Algorithm'])

# Create the grouped bar plot
plt.figure(figsize=(10, 6))
sns.barplot(data=df, x='Runs', y='Mean (ms)', hue='Algorithm')

# Format the plot
plt.title('Algorithm Scaling Performance (Mean Execution Time vs Runs)', fontsize=14)
plt.xlabel('Number of Runs', fontsize=12)
plt.ylabel('Mean Execution Time (ms)', fontsize=12)
plt.grid(True, axis='y', linestyle='--', alpha=0.7)
plt.legend(title='Algorithm')
plt.tight_layout()

# Save the plot
plt.savefig('dag_scaling_barplot.png')